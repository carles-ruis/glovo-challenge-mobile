package com.glovo.challenge.ui.city

import com.google.android.gms.maps.model.LatLng

interface MainView {

    fun showLoading()
    fun hideLoading()
    fun showSelectCityDialog(countriesCities: Map<String, List<String>>)
    fun showRetry()
    fun registerForCameraUpdates()
    fun setupMarkers(locations: List<Pair<String, LatLng>>)
    fun setMarkersVisibility(visible: Boolean)
    fun checkLocationPermission()
    fun displayWorkingArea(workingArea: List<LatLng>)
    fun moveToLocation(location: LatLng, zoom: Float)
}