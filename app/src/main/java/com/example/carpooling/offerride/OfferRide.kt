package com.example.carpooling.offerride

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase



@Composable
@Preview
fun OfferRideScreen() {

    val scrollState = rememberScrollState()

    val backgroundColor = if (isSystemInDarkTheme()) {
        Color.Black
    } else {
        Color.White
    }

    val context = LocalContext.current

    var pickupLocation by remember { mutableStateOf<LatLng?>(null) }
    var dropoffLocation by remember { mutableStateOf<LatLng?>(null) }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var availableSeats by remember { mutableStateOf("") }
    var nameofDriver by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 12f)
    }

    // Initialize Places API
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, "AIzaSyDIr59pQ94XAbWKOoN8NBEVY19bLU-dKtg") // Replace with your Google Places API Key
        }
    }

    // Launcher for pickup location search result
    val pickupLocationLauncher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val place = Autocomplete.getPlaceFromIntent(data)
                    pickupLocation = place.latLng
                    Toast.makeText(context, "Pickup Location: ${place.name}", Toast.LENGTH_SHORT).show()

                    // Update the camera position to the pickup location
                    pickupLocation?.let {
                        cameraPositionState.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(it, 12f)
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                Toast.makeText(context, "Pickup Location selection canceled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "Error occurred while selecting pickup location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Launcher for dropoff location search result
    val dropoffLocationLauncher: ActivityResultLauncher<Intent> = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                result.data?.let { data ->
                    val place = Autocomplete.getPlaceFromIntent(data)
                    dropoffLocation = place.latLng
                    Toast.makeText(context, "Dropoff Location: ${place.name}", Toast.LENGTH_SHORT).show()

                    // Update the camera position to the dropoff location
                    dropoffLocation?.let {
                        cameraPositionState.position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(it, 12f)
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                Toast.makeText(context, "Dropoff Location selection canceled", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(context, "Error occurred while selecting dropoff location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.height(35.dp))

        // Google Map for displaying selected locations
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                if (pickupLocation == null) {
                    pickupLocation = latLng
                    Toast.makeText(context, "Pickup location selected", Toast.LENGTH_SHORT).show()
                } else if (dropoffLocation == null) {
                    dropoffLocation = latLng
                    Toast.makeText(context, "Dropoff location selected", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            pickupLocation?.let {
                Marker(state = MarkerState(position = it), title = "Pickup Location")
            }
            dropoffLocation?.let {
                Marker(state = MarkerState(position = it), title = "Dropoff Location")
            }
        }

        // Input fields for ride details
        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date (e.g., 2024-10-10)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )

        OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time (e.g., 10:30 AM)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )

        OutlinedTextField(
            value = availableSeats,
            onValueChange = { availableSeats = it },
            label = { Text("Available Seats") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )

        OutlinedTextField(
            value = nameofDriver,
            onValueChange = { nameofDriver = it },
            label = { Text("Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        )

        // Search fields for Pickup and Dropoff locations
                Button(onClick = {
                    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
                    pickupLocationLauncher.launch(intent)
                }, colors = ButtonDefaults.buttonColors(Color(0xFFF49F0A)), shape = RoundedCornerShape(15.dp)) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Text(text = "Locate your Pickup Location",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(15.dp))
                }

                Button(onClick = {
                    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
                    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
                    dropoffLocationLauncher.launch(intent)
                }, colors = ButtonDefaults.buttonColors(Color(0xFFF49F0A)), shape = RoundedCornerShape(15.dp)) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Text(text = "Locate your DropOff Location",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(15.dp),)
                }

        // Offer Ride Button
        Button(
            onClick = {
                if (pickupLocation != null && dropoffLocation != null && date.isNotEmpty() && time.isNotEmpty() && availableSeats.isNotEmpty() && nameofDriver.isNotEmpty()) {
                    offerRideToRealtimeDatabase(
                        pickupLocation!!,
                        dropoffLocation!!,
                        date,
                        time,
                        availableSeats.toInt(),
                        nameofDriver,
                        context
                    )
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            },colors = ButtonDefaults.buttonColors(Color(0xFFF49F0A)),
             shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxWidth().padding(35.dp)
        ) {
            Text("Offer Ride")
        }
    }
}

// Helper function to offer a ride to Firebase Realtime Database
private fun offerRideToRealtimeDatabase(
    pickupLocation: LatLng,
    dropoffLocation: LatLng,
    date: String,
    time: String,
    seats: Int,
    nameofDriver: String,
    context: Context
) {
    FirebaseApp.initializeApp(context)
    val database = FirebaseDatabase.getInstance().reference

    val rideId = database.child("rides").push().key

    val rideDetails = hashMapOf(
        "pickupLocation" to mapOf(
            "latitude" to pickupLocation.latitude,
            "longitude" to pickupLocation.longitude
        ),
        "dropoffLocation" to mapOf(
            "latitude" to dropoffLocation.latitude,
            "longitude" to dropoffLocation.longitude
        ),
        "nameOfDriver" to nameofDriver,
        "date" to date,
        "time" to time,
        "availableSeats" to seats
    )

    if (rideId != null) {
        database.child("rides").child(rideId).setValue(rideDetails)
            .addOnSuccessListener {
                Toast.makeText(context, "Ride offered successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to offer ride", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "Error generating ride ID", Toast.LENGTH_SHORT).show()
    }
}

private fun openPickupLocationPicker(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
        .build(context)
    launcher.launch(intent)
}

private fun openDropoffLocationPicker(context: Context, launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
    val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
    val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
        .build(context)
    launcher.launch(intent)
}
