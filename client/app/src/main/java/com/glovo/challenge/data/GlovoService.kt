package com.glovo.challenge.data

import com.glovo.challenge.data.CityDto
import com.glovo.challenge.data.CountryDto
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GlovoService {

    @GET("countries")
    fun getCountries(): Single<List<CountryDto>>

    @GET("cities")
    fun getCities(): Single<List<CityDto>>

    @GET("cities/{cityCode}")
    fun getCity(@Path("cityCode") cityCode: String): Single<CityDto>
}