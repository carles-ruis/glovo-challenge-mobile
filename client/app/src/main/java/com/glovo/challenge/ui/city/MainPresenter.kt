package com.glovo.challenge.ui.city

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

    private val DEFAULT_ZOOM = 10f
    private val ZOOM_THRESHOLD = 5f

    private val disposables = CompositeDisposable()
    private var countryList = emptyList<Country>()
    private var cityList = emptyList<City>()
    private var currentCity: City? = null

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

    private fun onCityDetailsSuccess(city: City) {
    }

    private fun onCityDetailsError(throwable: Throwable) {

    }

    fun onRetryClick() {
        getCountriesAndCities()
    }

    fun onMyLocationSuccess(location: LatLng) {
        currentCity = findCityFromLocation(location)
        if (currentCity == null) {
            showSelectCityDialog()
        } else {
            view.moveToLocation(location, DEFAULT_ZOOM)
        }
    }

    private fun findCityFromLocation(location: LatLng) = cityList.firstOrNull {
        var found = false
        for (polygon in it.polygons) {
            if (PolyUtil.containsLocation(location, polygon, true)) found = true
        }
        found
    }

    fun onMyLocationFailed() {
        showSelectCityDialog()
    }

    private fun showSelectCityDialog() {
        val countryMap = countryList.map { it.code to it.name }.toMap()
        val countriesCities = TreeMap<String, MutableList<String>>()

        countryList.forEach { countriesCities.put(it.name, mutableListOf()) }
        cityList.forEach {
            countriesCities.get(countryMap.get(it.countryCode))?.add(it.name)
        }
        countriesCities.forEach { it.value.sort() }
        view.showSelectCityDialog(countriesCities.filter { it.key.isNotBlank() && it.value.isNotEmpty() })
    }

    fun onCitySelected(cityName: String) {
        currentCity = cityList.firstOrNull { city -> city.name == cityName }?.apply {
            view.moveToLocation(getCityLocation(this), DEFAULT_ZOOM)
        }
    }

    private fun getCityLocation(city: City) = city.polygons.get(0).get(0)

    fun onCameraUpdate(location: LatLng, zoom: Float) {
        /*       val newCity = findCityFromLocation(location)
               if (newCity == null) {
                   view.clearCityInfo()
               } else if (newCity != currentCity) {

                   if (hasFullInfo(newCity)) {
                       view.showCityInfo(newCity)
                   } else {
                       getCityDetails(newCity.code)
                   }
               }
               currentCity = newCity*/
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




