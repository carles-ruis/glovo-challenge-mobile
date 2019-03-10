package com.glovo.challenge.ui.city

import com.glovo.challenge.model.City
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions

interface MainView {

    fun showLoading()
    fun hideLoading()
    fun showSelectCityDialog(countriesCities: Map<String, List<String>>)
    fun showRetry()
    fun registerForCameraUpdates()
    fun setupMarkers(cityList: List<City>)
    fun setMarkersVisibility(visible: Boolean)
    fun checkLocationPermission()
    fun moveCamera(bounds: LatLngBounds)
    fun clearCityInfo()
    fun showCityInfo(city: City, country: String?)
    fun showPolygons(polygonOptions: PolygonOptions)
    fun removePolygons()
}