package com.glovo.challenge.data

data class CountryDto(val code: String, val name: String)

data class CityDto(
    val code: String,
    val name: String,
    val countryCode: String,
    val currency: String? = null,
    val enabled: Boolean? = null,
    val busy: Boolean? = null,
    val timeZone: String? = null,
    val workingArea: List<String> = emptyList()
)