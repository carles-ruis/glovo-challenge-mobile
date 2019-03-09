package com.glovo.challenge.data

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class GlovoNetworkDatasource() {

    private val BASE_URL = "http://192.168.1.102:3000/api/"

    private val apiService: GlovoService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GlovoService::class.java)
    }

    fun getCountries() = apiService.getCountries().flattenAsObservable { it.map { it.toModel() }}.toList()

    fun getCities() = apiService.getCities().flattenAsObservable { it.map { it.toModel() }}.toList()

    fun getCity(cityCode: String) = apiService.getCity(cityCode).map { it.toModel() }

}