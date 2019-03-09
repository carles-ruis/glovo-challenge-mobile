package com.glovo.challenge.ui.city

import android.util.Log
import com.glovo.challenge.model.City
import com.glovo.challenge.model.Country
import com.glovo.challenge.repository.CityRepository
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

class MainPresenter(val view: MainView, val repository: CityRepository) {


    fun onMapReady() {

        checkPermissions()

        getCountriesAndCities()

    }

    private fun checkPermissions() {}

    private fun getCountriesAndCities() {
        val countriesObservable = repository.getCountries()
        val citiesObservable = repository.getCities()

        Single.zip(countriesObservable, citiesObservable,
            BiFunction { countries: List<Country>, cities: List<City> -> Pair(countries, cities) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(::onCountriesAndCitiesSuccess, ::onCountriesAndCitiesError)
    }

    private fun onCountriesAndCitiesSuccess(result: Pair<List<Country>, List<City>>) {
        Log.e("carles", result.toString())
    }

    private fun onCountriesAndCitiesError(throwable: Throwable) {
        Log.e("carles", throwable.toString())
    }

    private fun getCityDetails(city: City) {
        if (city.code != null) {
            repository.getCity(city.code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::onCityDetailsSuccess, ::onCityDetailsError)
        }
    }

    private fun onCityDetailsSuccess(city: City) {
    }

    private fun onCityDetailsError(throwable: Throwable) {

    }

}




