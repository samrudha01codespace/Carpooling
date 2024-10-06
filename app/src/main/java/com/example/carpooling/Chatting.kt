package com.example.carpooling

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

data class ChatMessage(
    val sender: String = "",
    val message: String = "",
    val timestamp: Long = 0L
)

@Composable
fun ChatScreen(rideId: String, driverName: String) {
    var message by remember { mutableStateOf("") }
    var senderId by remember { mutableStateOf("") }
    val context = LocalContext.current
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }

    // Listen to chat messages in real-time
    LaunchedEffect(rideId) {
        Log.d("ChatScreen", "Listening to rideId: $rideId")  // Add this to confirm rideId
        val database = FirebaseDatabase.getInstance().reference.child("chats")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messages = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load messages", Toast.LENGTH_SHORT).show()
            }
        })
    }

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val database = FirebaseDatabase.getInstance().reference.child("users").child(userId.toString())
    database.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            senderId = snapshot.children.mapNotNull { it.getValue(ChatMessage::class.java) }.toString()
        }

        override fun onCancelled(error: DatabaseError) {
            Toast.makeText(context, "Failed to load messages", Toast.LENGTH_SHORT).show()
        }
    })


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages) { message ->
                Text(text = "${message.sender}: ${message.message}")
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter message") }
            )
            Button(onClick = {
                if (message.isNotEmpty()) {
                    sendMessage(message) // Assuming driver for now
                    message = ""
                }
            }) {
                Text("Send")
            }
        }
    }
}

// Send a message to Firebase
fun sendMessage(message: String) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    if (userId == null) {
        Log.e("ChatScreen", "User not authenticated")
        return
    }

    // Get a reference to the user's profile in the "users" node
    val userRef = FirebaseDatabase.getInstance().reference.child("users").child(userId)

    // Fetch the sender's name
    userRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val senderName = snapshot.child("name").getValue(String::class.java) ?: "Unknown"

            // Proceed with sending the message
            sendMessageWithSender(message, senderName)
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("ChatScreen", "Failed to fetch user profile: ${error.message}")
        }
    })
}

fun sendMessageWithSender(message: String, sender: String) {
    val rideId1 = FirebaseAuth.getInstance().currentUser?.uid?.replace(".", ",") ?: ""
    val database = FirebaseDatabase.getInstance().reference.child("chats").child(rideId1)
    val messageId = database.push().key

    if (messageId == null) {
        Log.e("ChatScreen", "Failed to send message: messageId is null")
        return
    }

    val chatMessage = ChatMessage(
        sender = sender,
        message = message,
        timestamp = System.currentTimeMillis()
    )

    // Try to send the message and log any failure
    database.child(messageId).setValue(chatMessage).addOnFailureListener {
        Log.e("ChatScreen", "Failed to send message: ${it.message}")
    }
}
