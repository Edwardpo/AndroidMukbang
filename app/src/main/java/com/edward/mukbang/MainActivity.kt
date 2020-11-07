package com.edward.mukbang

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = SupportMapFragment.newInstance(mapOptions())
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map as GoogleMap

        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    private fun mapOptions(): GoogleMapOptions {
        var mapOptions = GoogleMapOptions()
        // Allows us to also see indoor maps
        mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
        return mapOptions
    }

}