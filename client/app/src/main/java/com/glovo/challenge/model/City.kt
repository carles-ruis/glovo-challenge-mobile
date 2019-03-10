package com.glovo.challenge.model

import com.google.android.gms.maps.model.LatLng

data class City (
    val code: String,
    val name: String,
    val countryCode: String,
    val currency: String? = null,
    val enabled: Boolean? = null,
    val busy: Boolean? = null,
    val timeZone: String? = null,
    val workingArea: List<String> = emptyList(),
    var polygons: List<List<LatLng>> = emptyList()
)