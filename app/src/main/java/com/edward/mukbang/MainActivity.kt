package com.edward.mukbang

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = SupportMapFragment.newInstance(mapOptions())
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment).commit()
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map as GoogleMap
        enableLocation()
    }

    private fun mapOptions(): GoogleMapOptions {
        var mapOptions = GoogleMapOptions()
        // Allows us to also see indoor maps
        mapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL)
        return mapOptions
    }

    @SuppressLint("MissingPermission")
    private fun showCurrentLocation() {
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val currLocation = LatLng(location.latitude, location.longitude)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 16.0f))
        }
    }

    private fun enableLocation() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation()
        }
        else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOC_PERM_REQ_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOC_PERM_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showCurrentLocation()
        }
    }


    companion object {
        const val LOC_PERM_REQ_CODE = 100
    }
}