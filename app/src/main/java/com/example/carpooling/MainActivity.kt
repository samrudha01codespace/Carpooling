package com.example.carpooling

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.example.carpooling.offerride.finalview
import com.example.carpooling.ui.theme.CarpoolingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CarpoolingTheme {

                val context = LocalContext.current
                // Pass lambdas for onLoginSuccess and onNavigateToRegister
                LoginScreen(
                    onLoginSuccess = {

                        val intent = Intent(context, HomeScreenActivity::class.java)
                        context.startActivity(intent)

                    },
                    onNavigateToRegister = {
                        val intent = Intent(context, RegisterActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}
