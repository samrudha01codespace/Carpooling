package com.example.carpooling.offerride.Activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.carpooling.offerride.OfferRideScreen
import com.example.carpooling.offerride.offerRide

class OfferRideActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            OfferRideScreen()

        }
    }
}
