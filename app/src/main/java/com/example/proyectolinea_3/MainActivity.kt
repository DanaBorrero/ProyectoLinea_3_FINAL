package com.example.proyectolinea_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyectolinea_3.Screen.Navigation
import com.example.proyectolinea_3.ui.theme.ProyectoLinea3Theme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        Thread.sleep(1000)
        splashScreen.setKeepOnScreenCondition { false }

        setContent {
            ProyectoLinea3Theme {
                Navigation()
            }
        }
    }
}
