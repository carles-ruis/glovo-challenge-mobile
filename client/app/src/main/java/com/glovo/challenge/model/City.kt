package com.glovo.challenge.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions

data class City (
    val code: String,
    val name: String,
    val countryCode: String,
    var currency: String? = null,
    val enabled: Boolean? = null,
    val busy: Boolean? = null,
    var timeZone: String? = null,
    val workingArea: List<String> = emptyList(),
    var polygons: List<List<LatLng>> = emptyList(),
    var polygon: PolygonOptions? = null,
    var center: LatLng? = null
)