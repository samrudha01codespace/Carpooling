package com.example.carpooling.offerride

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carpooling.R
import com.example.carpooling.offerride.Activity.OfferRideActivity
import com.example.carpooling.requestride.Activity.RequestRideActivity
import com.google.maps.android.compose.*
import java.util.*


@Composable
@Preview
fun title() {
    val backgroundColor1 = if (isSystemInDarkTheme()) {
        Color.White // Set this to a darker color
    } else {
        Color.Black // Set this to a lighter color or theme color for light mode
    }
    Text(
        text = "Offer a Ride",
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp,
        color = backgroundColor1
    )
}

@Composable
@Preview
fun offerRide() {

    val context = LocalContext.current
    val intent = Intent(context, OfferRideActivity::class.java)

    Button(
        onClick = { context.startActivity(intent) },
        colors = ButtonDefaults.buttonColors(Color(0xFFF49F0A))
    ) {
        Text(text = "Offer a Ride")
    }

}

@Composable
@Preview
fun requestRide() {
    val context = LocalContext.current
    val intent = Intent(context, RequestRideActivity::class.java)

    Button(onClick = { context.startActivity(intent) }, colors = ButtonDefaults.buttonColors(Color(0xFFF49F0A))) {
        Text(text = "Request a Ride")
    }

}

@Composable
@Preview
fun carImage() {


    Image(
        painter = painterResource(id = R.drawable.car1),
        contentDescription = "Car Image",
        modifier = Modifier
            .padding(50.dp)
            .fillMaxWidth()
    )


}


@Composable
@Preview
fun finalview() {
    val backgroundColor = if (isSystemInDarkTheme()) {
        Color.Black // Set this to a darker color
    } else {
        Color.White // Set this to a lighter color or theme color for light mode
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(top = 25.dp))
        title()
        carImage()
        Spacer(modifier = Modifier.padding(top = 25.dp))
        offerRide()
        Spacer(modifier = Modifier.padding(top = 25.dp))
        requestRide()

    }
}





