package com.glovo.challenge.repository

import com.glovo.challenge.data.GlovoNetworkDatasource

class CityRepository(val datasource: GlovoNetworkDatasource) {

    fun getCountries() = datasource.getCountries()

    fun getCities() = datasource.getCities()

    fun getCity(cityCode: String) = datasource.getCity(cityCode)

}