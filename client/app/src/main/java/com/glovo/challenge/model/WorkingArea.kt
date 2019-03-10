package com.glovo.challenge.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions

data class WorkingArea(
    val areas: List<List<LatLng>>,
    val polygonOptions: PolygonOptions,
    val bounds: LatLngBounds)