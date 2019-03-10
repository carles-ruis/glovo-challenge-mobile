package com.glovo.challenge.data

import com.glovo.challenge.model.City
import com.glovo.challenge.model.Country
import com.glovo.challenge.model.WorkingArea
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.PolyUtil

fun CountryDto.toModel() = Country(code, name)

fun CityDto.toModel() = City(code, name, countryCode, currency, enabled, busy, timeZone, toWorkingAreaModel())

fun CityDto.toWorkingAreaModel(): WorkingArea {
    val areas = workingArea.map { PolyUtil.decode(it) }.filter { it.isNotEmpty() }

    val polygonOptions = PolygonOptions()
    val builder = LatLngBounds.Builder()
    for (locationList in areas) {
        polygonOptions.addAll(locationList)
        for (location in locationList) {
            builder.include(location)
        }
    }

    val bounds = builder.build()
    return WorkingArea(areas, polygonOptions, bounds)
}