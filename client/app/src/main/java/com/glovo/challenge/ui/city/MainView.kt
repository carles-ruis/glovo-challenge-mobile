package com.glovo.challenge.ui.city

import com.glovo.challenge.model.City
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions

interface MainView {

    fun showLoading()
    fun hideLoading()
    fun showSelectCityDialog(countriesCities: Map<String, List<String>>)
    fun showRetry()
    fun registerForCameraUpdates()
    fun setupMarkers(locations: List<Pair<String, LatLng>>)
    fun setMarkersVisibility(visible: Boolean)
    fun checkLocationPermission()
    fun moveToLocation(location: LatLng, zoom: Float)
    fun clearCityInfo()
    fun showCityInfo(city: City, country: String?)
    fun showPolygons(polygonOptions: PolygonOptions)
    fun removePolygons()
}