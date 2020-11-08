package com.edward.mukbang

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceLikelihood
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Places.initialize(applicationContext, getString(R.string.maps_api_key))

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
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            populateMap()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOC_PERM_REQ_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOC_PERM_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            populateMap()
        }
    }

    private fun populateMap() {
        showCurrentLocation()
        findNearbyRestaurants(completion = { nearbyRestaurants ->
            for (nearbyRestaurant in nearbyRestaurants) {
                map.addMarker(
                    MarkerOptions()
                        .title(nearbyRestaurant.name)
                        .position(nearbyRestaurant.latLng!!)
                )
            }
        })
        map.setInfoWindowAdapter(VideoInfoWindowAdapter(this))
    }

    @SuppressLint("MissingPermission")
    private fun findNearbyRestaurants(completion: (List<Place>) -> Unit) {
        val placesClient = Places.createClient(this)
        val placeResponse = placesClient.findCurrentPlace(nearbyPlacesRequest())
        placeResponse.addOnCompleteListener { task ->
            val restaurants = ArrayList<Place>()
            if (task.isSuccessful) {
                val response = task.result
                for (placeLikelihood: PlaceLikelihood in response.placeLikelihoods) {
                    placeLikelihood.place.types?.let { placeTypes ->
                        if (placeTypes.contains(Place.Type.FOOD) || placeTypes.contains(Place.Type.RESTAURANT)) {
                            restaurants.add(placeLikelihood.place)
                        }
                    }
                }
                completion(restaurants)
            } else {
                val exception = task.exception
                Log.e(LOG_TAG, "Place not found: $exception")
                completion(restaurants)
            }

        }
    }

    private fun nearbyPlacesRequest(): FindCurrentPlaceRequest {
        return FindCurrentPlaceRequest.newInstance(
            listOf(
                Place.Field.ID,
                Place.Field.LAT_LNG,
                Place.Field.NAME,
                Place.Field.RATING,
                Place.Field.TYPES
            )
        )
    }

    companion object {
        val LOG_TAG = MainActivity.javaClass.simpleName
        const val LOC_PERM_REQ_CODE = 100
    }
}