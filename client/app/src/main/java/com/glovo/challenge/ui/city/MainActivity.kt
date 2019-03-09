package com.glovo.challenge.ui.city

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.glovo.challenge.R
import com.glovo.challenge.data.GlovoNetworkDatasource
import com.glovo.challenge.repository.CityRepository
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private val presenter : MainPresenter
    init {
        val datasource = GlovoNetworkDatasource()
        val repository = CityRepository(datasource)
        presenter = MainPresenter(this, repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        initViews()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.main_map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    private fun initComponents() {

    }

    private fun initViews() {
        setSupportActionBar(main_toolbar)
        main_toolbar.setNavigationOnClickListener { finish() }

        main_city_textview.text = "City: Barcelona, Catalonia"
        main_currency_textview.text = "Currency: EUR"
        main_timezone_textview.text = "Timezone: GMT"
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

    fun showSelectCityDialog(countries: List<String>, cities: List<String>) {
        SelectCityDialog(this, countries, cities).show()
    }
}