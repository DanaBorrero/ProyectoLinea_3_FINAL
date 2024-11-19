@file:Suppress("UNUSED_EXPRESSION")

package com.example.proyectolinea_3.Screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectolinea_3.Screen.Horario.MapWithRoutes
import com.example.proyectolinea_3.Screen.Inicio.Inicio
import com.example.proyectolinea_3.Screen.Report.utilities.CameraPermission
import com.example.proyectolinea_3.Screen.Report.ReportScreen
import com.example.proyectolinea_3.Screen.Report.utilities.MapScreen
import com.example.proyectolinea_3.Screen.Login.Login
import com.example.proyectolinea_3.Screen.Recoleccionscreen.ListaSolicitudesScreen
import com.example.proyectolinea_3.Screen.Recoleccionscreen.Recoleccion
import com.example.proyectolinea_3.Screen.Register.Register
import com.example.proyectolinea_3.Screen.Report.EditarEliminarReporteScreen
import com.example.proyectolinea_3.Screen.Report.ListReportScreen
import com.example.proyectolinea_3.Screen.Report.utilities.SharedViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val sharedViewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

    NavHost(navController = navController, startDestination = "Login") { // Cambia aquí a "SplashScreen"
        composable("SplashScreen") {
            SplashScreen(navController)
        }

        composable("Login") {
            Login(navController)
        }

        composable("home") {
            Inicio(navController)
        }

        composable("Recoleccion") {
            Recoleccion(navController)
        }
        composable("ListarSolicitudes"){
            ListaSolicitudesScreen(navController)
        }

        composable("Register") {
            Register(navController)
        }

        composable("Reporte") {
            ReportScreen(navController, "", "", null)
        }

        composable("Map/{description}/{imageUri}",
            arguments = listOf(
                navArgument("description") { type = NavType.StringType },
                navArgument("imageUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri")?.let { Uri.parse(it) }
            val description = backStackEntry.arguments?.getString("description") ?: ""

            MapScreen(navController = navController, description = description, imageUri = imageUri)
        }

        composable("Camara/{description}/{location}",
            arguments = listOf(
                navArgument("description") { type = NavType.StringType },
                navArgument("location") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val location = backStackEntry.arguments?.getString("location") ?: ""

            CameraPermission(navController, description, location)
        }

        composable(
            "ReportScreen/{description}/{location}/{imageUri}",
            arguments = listOf(
                navArgument("description") { type = NavType.StringType },
                navArgument("location") { type = NavType.StringType },
                navArgument("imageUri") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val description = backStackEntry.arguments?.getString("description") ?: ""
            val location = backStackEntry.arguments?.getString("location") ?: ""
            val imageUri = backStackEntry.arguments?.getString("imageUri")?.let { Uri.parse(Uri.decode(it)) }

            ReportScreen(navController, description, location, imageUri)
        }

        composable("ListaReportes") {
            ListReportScreen(navController = navController, sharedViewModel = sharedViewModel)
        }

        composable("EditarReporte") {
            val context = LocalContext.current
            EditarEliminarReporteScreen(
                navController = navController,
                sharedViewModel = sharedViewModel
            )
        }


        composable("MapaRutas") {
            MapWithRoutes(navController = navController)
        }

    }
}



