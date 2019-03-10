package com.glovo.challenge.ui.city

import android.util.Log
import com.glovo.challenge.model.City
import com.glovo.challenge.model.Country
import com.glovo.challenge.repository.CityRepository
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.PolyUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainPresenter(val view: MainView, val repository: CityRepository) {

    private val DEFAULT_ZOOM = 11f
    private val ZOOM_THRESHOLD = 9f
    private val POLYGON_COARSE_TOLERANCE_METERS = 15000.0
    private val POLYGON_FINE_TOLERANCE_METERS = 5000.0

    private val disposables = CompositeDisposable()
    private var countryList = emptyList<Country>()
    private var cityList = emptyList<City>()
    private var countryMap = emptyMap<String, String>()
    private var currentCity: City? = null
    private var currentZoom: Float = DEFAULT_ZOOM
    private var isShowingMarkers = false
    private var isShowingPolygons = false

    fun onMapReady() {
        getCountriesAndCities()
    }

    private fun getCountriesAndCities() {
        view.showLoading()

        val countriesObservable = repository.getCountries()
        val citiesObservable = repository.getCities().doOnSuccess { cityList ->
            for (city in cityList) {
                val polygons = mutableListOf<List<LatLng>>()
                for (workingArea in city.workingArea) {
                    polygons.add(PolyUtil.decode(workingArea))
                }
                city.polygons = polygons.filter { it.isNotEmpty() }
            }
        }

        addDisposable(
            Single.zip(countriesObservable, citiesObservable,
                BiFunction { countries: List<Country>, cities: List<City> -> Pair(countries, cities) })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onCountriesAndCitiesSuccess, ::onCountriesAndCitiesError)
        )
    }

    private fun onCountriesAndCitiesSuccess(result: Pair<List<Country>, List<City>>) {
        this.countryList = result.first
        this.cityList = result.second
        this.countryMap = countryList.map { it.code to it.name }.toMap()

        view.hideLoading()
        view.registerForCameraUpdates()
        view.setupMarkers(obtainMarkerLocations())
        view.setMarkersVisibility(false)
        view.checkLocationPermission()
    }

    private fun obtainMarkerLocations(): List<Pair<String, LatLng>> {
        val markerLocations = mutableListOf<Pair<String, LatLng>>()
        for (city in cityList) {
            markerLocations.add(Pair(city.name, getCityLocation(city)))
        }
        return markerLocations
    }

    private fun onCountriesAndCitiesError(throwable: Throwable) {
        view.showRetry()
    }

    private fun getCityDetails(code: String) {
        addDisposable(
            repository.getCity(code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onCityDetailsSuccess, ::onCityDetailsError)
        )
    }

    private fun onCityDetailsSuccess(cityDetail: City) {
        cityList.first { city -> city.code == cityDetail.code }.apply {
            if (this == currentCity) {
                currency = cityDetail.currency
                timeZone = cityDetail.timeZone
                view.showCityInfo(this, countryMap.get(countryCode))
            }
        }
    }

    private fun onCityDetailsError(throwable: Throwable) {
        Log.w(javaClass.simpleName, "onCityDetailsError: " + throwable.message)
    }

    fun onRetryClick() {
        getCountriesAndCities()
    }

    fun onMyLocationSuccess(location: LatLng) {
        val myLocationCity = findCityFromLocation(location)
        if (myLocationCity == null) {
            showSelectCityDialog()
        } else {
            view.moveToLocation(location, DEFAULT_ZOOM)
        }
    }

    private fun findCityFromLocation(location: LatLng): City? {
        var coarseCloseCity: City? = null
        for (city in cityList) {
            for (polygon in city.polygons) {
                if (PolyUtil.isLocationOnPath(location, polygon, true, POLYGON_FINE_TOLERANCE_METERS)) {
                    return city
                } else if (PolyUtil.isLocationOnPath(location, polygon, true, POLYGON_COARSE_TOLERANCE_METERS)) {
                    coarseCloseCity = city
                }
            }
        }
        return coarseCloseCity
    }

    fun onMyLocationFailed() {
        showSelectCityDialog()
    }

    private fun showSelectCityDialog() {
        val countriesCities = TreeMap<String, MutableList<String>>()

        countryList.forEach { countriesCities.put(it.name, mutableListOf()) }
        cityList.forEach {
            countriesCities.get(countryMap.get(it.countryCode))?.add(it.name)
        }
        countriesCities.forEach { it.value.sort() }
        view.showSelectCityDialog(countriesCities.filter { it.key.isNotBlank() && it.value.isNotEmpty() })
    }

    fun onCitySelected(cityName: String) {
        cityList.firstOrNull { city -> city.name == cityName }?.apply {
            view.moveToLocation(getCityLocation(this), DEFAULT_ZOOM)
        }
    }

    private fun getCityLocation(city: City) = city.polygons.get(0).get(0)

    fun onCameraUpdate(location: LatLng, zoom: Float) {
        val newCity = findCityFromLocation(location)
        if (newCity != currentCity) {
            if (newCity == null) {
                view.clearCityInfo()
            } else {
                view.showCityInfo(newCity, countryMap.get(newCity.countryCode))
                if (!hasFullInfo(newCity)) {
                    getCityDetails(newCity.code)
                }
            }
        }

        if (zoom < ZOOM_THRESHOLD && !isShowingMarkers) {
            isShowingMarkers = true
            isShowingPolygons = false
            view.setMarkersVisibility(true)
            view.removePolygons()
        } else if (zoom >= ZOOM_THRESHOLD && (!isShowingPolygons || newCity != currentCity)) {
            isShowingMarkers = false
            isShowingPolygons = true
            view.setMarkersVisibility(false)
            showPolygons(newCity?.polygons)
        }

        currentCity = newCity
        currentZoom = zoom
    }

    private fun showPolygons(locationListOfList: List<List<LatLng>>?) {
        if (locationListOfList == null) {
            return
        }
        addDisposable(Single.create<PolygonOptions> { emitter ->
            val polygonOptions = PolygonOptions()
            for (locationList in locationListOfList) {
                polygonOptions.addAll(locationList)
            }
            emitter.onSuccess(polygonOptions)
        }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { polygonOptions ->
                view.showPolygons(polygonOptions)
            })
    }

    private fun hasFullInfo(city: City) = city.currency != null && city.timeZone != null

    fun onMarkerClick(marker: Marker) {
        view.moveToLocation(marker.position, DEFAULT_ZOOM)
    }

    fun onViewDestroyed() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable);
    }
}