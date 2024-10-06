package com.example.carpooling.offerride

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapIntegration(private val context: Context) : OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private var pickupMarker: Marker? = null
    private var dropoffMarker: Marker? = null

    // Callback when the map is ready to use
    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set up listeners for map click to select pickup or drop-off locations
        setupMapClickListeners()
    }

    private fun setupMapClickListeners() {
        // Map click listener for pickup location
        googleMap.setOnMapClickListener { latLng ->
            if (pickupMarker == null) {
                // First tap: Set the pickup location
                pickupMarker = googleMap.addMarker(
                    MarkerOptions().position(latLng).title("Pickup Location")
                )
                val address = getAddressFromLatLng(latLng)
                Log.d("MapIntegration", "Pickup selected: $address")

            } else if (dropoffMarker == null) {
                // Second tap: Set the drop-off location
                dropoffMarker = googleMap.addMarker(
                    MarkerOptions().position(latLng).title("Drop-off Location")
                )
                val address = getAddressFromLatLng(latLng)
                Log.d("MapIntegration", "Drop-off selected: $address")

            } else {
                Log.d("MapIntegration", "Both pickup and drop-off locations are set.")
            }
        }
    }

    // Method to get the address from latitude and longitude using Geocoder
    private fun getAddressFromLatLng(latLng: LatLng): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        if (addresses != null) {
            return if (addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                null
            }
        }
        return latLng.toString()
    }

    // Function to clear markers
    fun clearMarkers() {
        pickupMarker?.remove()
        dropoffMarker?.remove()
        pickupMarker = null
        dropoffMarker = null
    }

    // Zoom in to a specific LatLng on the map
    fun zoomToLocation(latLng: LatLng, zoomLevel: Float = 15f) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
    }
}
