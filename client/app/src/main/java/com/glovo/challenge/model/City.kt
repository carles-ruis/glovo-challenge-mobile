package com.glovo.challenge.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.PolyUtil

data class City(
    val code: String,
    val name: String,
    val countryCode: String,
    var currency: String? = null,
    val enabled: Boolean? = null,
    val busy: Boolean? = null,
    var timeZone: String? = null,
    val workingArea: List<String> = emptyList()
) {

    val workingAreaLatLng: List<List<LatLng>>
    val polygonOptions: PolygonOptions
    val bounds: LatLngBounds
    val center get() = bounds.center

    init {
        this.workingAreaLatLng = workingArea.map { PolyUtil.decode(it) }.filter { it.isNotEmpty() }

        val polygonOptions = PolygonOptions()
        val builder = LatLngBounds.Builder()
        for (locationList in workingAreaLatLng) {
            polygonOptions.addAll(locationList)
            for (location in locationList) {
                builder.include(location)
            }
        }
        this.polygonOptions = polygonOptions
        this.bounds = builder.build()
    }
}