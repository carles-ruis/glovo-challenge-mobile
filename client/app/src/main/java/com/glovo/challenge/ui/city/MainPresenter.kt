package com.glovo.challenge.ui.city

import android.location.Location
import android.util.Log
import com.glovo.challenge.model.City
import com.glovo.challenge.model.Country
import com.glovo.challenge.repository.CityRepository
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.util.*

class MainPresenter(val view: MainView, val repository: CityRepository) {

    private val ZOOM_THRESHOLD = 9f
    private val POLYGON_COARSE_TOLERANCE_METERS = 15000.0
    private val POLYGON_FINE_TOLERANCE_METERS = 2000.0

    private val disposables = CompositeDisposable()
    private var countryList = emptyList<Country>()
    private var cityList = emptyList<City>()
    private var countryMap = emptyMap<String, String>()
    private var currentCity: City? = null
    private var isShowingMarkers = false
    private var polygonCity : City? = null

    fun onMapReady() {
        getCountriesAndCities()
    }

    private fun getCountriesAndCities() {
        view.showLoading()

        val countriesObservable = repository.getCountries()
        val citiesObservable = repository.getCities()

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
        view.setupMarkers(cityList)
        view.setMarkersVisibility(false)
        view.checkLocationPermission()
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

    fun onMyLocationSuccess(location: Location) {
        val myLocationCity = findCityFromLocation(LatLng(location.latitude, location.longitude))
        if (myLocationCity == null) {
            showSelectCityDialog()
        } else {
            view.moveCamera(myLocationCity.workingArea.bounds)
        }
    }

    private fun findCityFromLocation(location: LatLng): City? {
        var coarseCloseCity: City? = null
        for (city in cityList) {
            for (locationList in city.workingArea.areas) {
                if (PolyUtil.isLocationOnPath(location, locationList, true, POLYGON_FINE_TOLERANCE_METERS)) {
                    return city
                } else if (PolyUtil.isLocationOnPath(location, locationList, true, POLYGON_COARSE_TOLERANCE_METERS)) {
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
        val citySelected = cityList.firstOrNull { city -> city.name == cityName }
        if (citySelected != null) {
            view.moveCamera(citySelected.workingArea.bounds)
        }
    }

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

        if (zoom < ZOOM_THRESHOLD) {
            if (polygonCity != null) {
                polygonCity = null
                view.removePolygons()
            }
            if (!isShowingMarkers) {
                isShowingMarkers = true
                view.setMarkersVisibility(true)
            }

        } else if (zoom >= ZOOM_THRESHOLD) {
            if (isShowingMarkers) {
                isShowingMarkers = false
                view.setMarkersVisibility(false)
            }
            if (newCity != null && newCity != polygonCity ) {
                polygonCity = newCity
                view.showPolygons(newCity.workingArea.polygonOptions)
            }
        }

        currentCity = newCity
    }

    private fun hasFullInfo(city: City) = city.currency != null && city.timeZone != null

    fun onViewDestroyed() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }

    private fun addDisposable(disposable: Disposable) {
        disposables.add(disposable);
    }
}