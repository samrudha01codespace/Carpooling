package com.example.carpooling.requestride

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpooling.ChattingActivity
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


data class Ride(
    val id: String = "",
    val pickupLocation: Double = 0.0,
    val dropoffLocation: Double = 0.0,
    val date: String = "",
    val time: String = "",
    val availableSeats: Int = 0,
    val nameOfDriver: String = ""
)

@Composable
fun MyListScreen() {
    var selectedRide by remember { mutableStateOf<Ride?>(null) }
    val context = LocalContext.current
    var rides by remember { mutableStateOf<List<Ride>>(emptyList()) }

    LaunchedEffect(Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("rides").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    rides = snapshot.children.mapNotNull { rideSnapshot ->
                        val pickupLocationSnapshot = rideSnapshot.child("pickupLocation")
                        val pickupLat = pickupLocationSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                        val pickupLng = pickupLocationSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0
                        val dropoffLocationSnapshot = rideSnapshot.child("dropoffLocation")
                        val dropoffLat = dropoffLocationSnapshot.child("latitude").getValue(Double::class.java) ?: 0.0
                        val dropoffLng = dropoffLocationSnapshot.child("longitude").getValue(Double::class.java) ?: 0.0

                        val date = rideSnapshot.child("date").getValue(String::class.java) ?: ""
                        val time = rideSnapshot.child("time").getValue(String::class.java) ?: ""
                        val availableSeats = rideSnapshot.child("availableSeats").getValue(Int::class.java) ?: 0
                        val nameOfDriver = rideSnapshot.child("nameOfDriver").getValue(String::class.java) ?: ""

                        Ride(
                            id = rideSnapshot.key ?: "",
                            pickupLocation = pickupLat,  // Only using latitude for simplicity
                            dropoffLocation = dropoffLng,  // Only using longitude for simplicity
                            date = date,
                            time = time,
                            availableSeats = availableSeats,
                            nameOfDriver = nameOfDriver
                        )
                    }
                } else {
                    Toast.makeText(context, "No rides available", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load rides: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(rides) { ride ->
            ListItemCard(ride = ride) {
                selectedRide = ride // Set the selected ride when clicked
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    // Only show Ride Details when a ride is selected
    selectedRide?.let { ride ->
        ShowRideDetails(
            ride = ride,
            onChatClick = {
                // This is what happens when "Chat with Driver" is clicked
                val intent = Intent(context, ChattingActivity::class.java)
                context.startActivity(intent)
                // You can navigate to a chat screen or open a chat feature here
            },
            onDismiss = {
                selectedRide = null // Dismiss the dialog when user clicks OK
            }
        )
    }
}


@Composable
fun ListItemCard(ride: Ride, onClick: () -> Unit) {  // onClick is now a regular lambda
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },  // Pass the onClick lambda here correctly
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = ride.nameOfDriver,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pickup: ${ride.pickupLocation} - Dropoff: ${ride.dropoffLocation}",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun ShowRideDetails(ride: Ride, onChatClick: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Ride Details") },
        text = {
            Column {
                Text("Driver: ${ride.nameOfDriver}")
                Text("Pickup Location: ${ride.pickupLocation}")
                Text("Dropoff Location: ${ride.dropoffLocation}")
                Text("Date: ${ride.date}")
                Text("Time: ${ride.time}")
                Text("Available Seats: ${ride.availableSeats}")
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onChatClick) {
                Text("Chat with Driver")
            }
        }
    )
}


