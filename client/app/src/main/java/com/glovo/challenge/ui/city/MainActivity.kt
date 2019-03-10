package com.glovo.challenge.ui.city

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View.GONE
import android.view.View.VISIBLE
import com.glovo.challenge.data.GlovoNetworkDatasource
import com.glovo.challenge.repository.CityRepository
import com.glovo.challenge.ui.REQUEST_PERMISSION_LOCATION
import com.glovo.challenge.ui.checkSelfPermissionCompat
import com.glovo.challenge.ui.requestPermissionsCompat
import com.google.android.gms.location.LocationServices.getFusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.view_progress.*


class MainActivity : AppCompatActivity(), MainView, OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var mMap: GoogleMap
    private val markers = mutableListOf<Marker>()
    private val presenter: MainPresenter

    init {
        val datasource = GlovoNetworkDatasource()
        val repository = CityRepository(datasource)
        presenter = MainPresenter(this, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.glovo.challenge.R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        setSupportActionBar(main_toolbar)
        main_toolbar.setNavigationOnClickListener { finish() }

        main_city_textview.text = "City: Barcelona, Catalonia"
        main_currency_textview.text = "Currency: EUR"
        main_timezone_textview.text = "Timezone: GMT"

        mapFragment =
                supportFragmentManager.findFragmentById(com.glovo.challenge.R.id.main_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        presenter.onMapReady()
/*
        val countries = listOf("1", "2", "3", "4")
        val cities = emptyList<String>()

        Handler().postDelayed({ showSelectCityDialog(countries, cities) }, 2000)
*/
        //     mMap = googleMap

        // Add a marker in Sydney and move the camera
        /*      val sydney = LatLng(-34.0, 151.0)
              mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
              mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }

    override fun onDestroy() {
        presenter.onViewDestroyed()
        super.onDestroy()
    }

    override fun showLoading() {
        progress.visibility = VISIBLE
    }

    override fun hideLoading() {
        progress.visibility = GONE
    }

    override fun showSelectCityDialog(countriesCities: Map<String, List<String>>) {
        SelectCityDialog(this, countriesCities) { city -> presenter.onCitySelected(city) }.show()
    }

    override fun showRetry() {
        AlertDialog.Builder(this).setMessage(com.glovo.challenge.R.string.main_retry_message).setCancelable(false)
            .setPositiveButton(com.glovo.challenge.R.string.main_retry_button) { _, _ -> presenter.onRetryClick() }
            .show()
    }

    override fun registerForCameraUpdates() {
        mMap.setOnCameraIdleListener {
            presenter.onCameraUpdate(mMap.cameraPosition.target, mMap.cameraPosition.zoom)
        }
    }

    override fun setupMarkers(locations: List<Pair<String, LatLng>>) {
        for (location in locations) {
            markers.add(mMap.addMarker(MarkerOptions().title(location.first).position(location.second)))
        }
   }

    override fun setMarkersVisibility(visible:Boolean) {
        for (marker in markers) {
            marker.isVisible = visible
        }
    }

    override fun checkLocationPermission() {
        if (checkSelfPermissionCompat(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setupMyLocation()
        } else {
            requestPermissionsCompat(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupMyLocation()
            } else {
                presenter.onMyLocationFailed()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupMyLocation() {
        mMap.isMyLocationEnabled = true
        getFusedLocationProviderClient(this).getLastLocation().addOnSuccessListener { location ->
            if (location != null) {
                presenter.onMyLocationSuccess(LatLng(location.latitude, location.longitude))
            }
        }.addOnFailureListener { presenter.onMyLocationFailed() }
    }

    override fun displayWorkingArea(workingArea: List<LatLng>) {
        // Do something with your decoded polyline. For example drawing it on your map
        mMap.addPolygon(
            PolygonOptions().fillColor(
                ContextCompat.getColor(
                    this,
                    com.glovo.challenge.R.color.red
                )
            ).strokeColor(ContextCompat.getColor(this, com.glovo.challenge.R.color.blue)).addAll(workingArea)
        );
    }

    override fun moveToLocation(location: LatLng, zoom: Float) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
    }


}