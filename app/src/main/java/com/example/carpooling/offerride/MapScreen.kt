package com.example.carpooling.offerride

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnSuccessListener
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


private val LOCATION_PERMISSION_REQUEST_CODE = 2
private lateinit var locationManager: LocationManager
private var latitude: Double? = null
private var longitude: Double? = null

@Composable
@Preview
fun MapScreen() {
    val context = LocalContext.current
    var pickupLocation by remember { mutableStateOf<LatLng?>(null) }
    var dropoffLocation by remember { mutableStateOf<LatLng?>(null) }
    var loadingPickup by remember { mutableStateOf(false) }
    var loadingDropoff by remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 12f)  // Default location: San Francisco
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            // Allow quick selections
            if (pickupLocation == null) {
                pickupLocation = latLng
                // Fetch address for pickup in the background
                loadingPickup = true
                fetchAddress(context, latLng) { address ->
                    loadingPickup = false
                    println("Pickup Location: $address")
                }
            } else if (dropoffLocation == null) {
                dropoffLocation = latLng
                // Fetch address for dropoff in the background
                loadingDropoff = true
                fetchAddress(context, latLng) { address ->
                    loadingDropoff = false
                    println("Dropoff Location: $address")
                }
            } else {
                println("Both locations selected. Clear to reselect.")
            }
        }
    ) {
        // Add markers for pickup and drop-off if set
        pickupLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Pickup Location"
            )
        }
        dropoffLocation?.let {
            Marker(
                state = MarkerState(position = it),
                title = "Dropoff Location"
            )
        }
    }

    // Show loading indicator only when necessary
    if (loadingPickup) {
        CircularProgressIndicator()
    }
    if (loadingDropoff) {
        CircularProgressIndicator()
    }
}

// Function to fetch address in the background
private fun fetchAddress(context: Context, latLng: LatLng, onAddressFetched: (String?) -> Unit) {
    val geocoder = Geocoder(context, Locale.getDefault())
    CoroutineScope(Dispatchers.IO).launch {
        delay(50)  // Add a slight delay to debounce rapid clicks
        val addresses = try {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

        val address = addresses?.let {
            if (it.isNotEmpty()) {
                it[0].getAddressLine(0)
            } else {
                null
            }
        }
        withContext(Dispatchers.Main) {
            onAddressFetched(address)
        }
    }
}

@Composable
fun GetLocationAndRegister() {
    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    // Request permission if not granted
    val locationPermission = remember { mutableStateOf(false) }

    // Launch permission request
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted: Boolean ->
            locationPermission.value = isGranted
            if (isGranted) {
                fetchCurrentLocation(fusedLocationClient, context)
            } else {
                Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    )

    // Check for permissions and fetch location
    LaunchedEffect(locationPermission.value) {
        if (!locationPermission.value) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fetchCurrentLocation(fusedLocationClient, context)
        }
    }
}

private fun fetchCurrentLocation(fusedLocationClient: FusedLocationProviderClient, context: Context) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        fusedLocationClient.lastLocation
            .addOnSuccessListener(context as Activity, OnSuccessListener { location: Location? ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    // Use the location values (latitude and longitude) as needed
                    println("Current Location: Latitude: $latitude, Longitude: $longitude")
                } else {
                    Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
                }
            })
    } else {
        Toast.makeText(context, "Location permission not granted", Toast.LENGTH_SHORT).show()
    }
}

