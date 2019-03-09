package com.glovo.challenge.data

import com.glovo.challenge.model.City
import com.glovo.challenge.model.Country

fun CountryDto.toModel() = Country(code , name)

fun CityDto.toModel() = City(code, name, countryCode, currency, enabled, busy, timeZone, workingArea)