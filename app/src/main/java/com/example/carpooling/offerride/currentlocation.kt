package com.example.carpooling.offerride

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnSuccessListener

class currentlocation {

    private var latitude: Double? = null
    private var longitude: Double? = null

    private fun fetchCurrentLocation(fusedLocationClient: FusedLocationProviderClient, context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener(context as Activity, OnSuccessListener { location: Location? ->
                    if (location != null) {
                        location.latitude
                        location.longitude
                    } else {
                        Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
        }
    }
}