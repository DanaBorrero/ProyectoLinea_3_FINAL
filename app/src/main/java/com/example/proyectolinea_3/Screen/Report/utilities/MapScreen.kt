package com.example.proyectolinea_3.Screen.Report.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import com.google.maps.android.compose.Marker


import android.location.Geocoder
import android.widget.Toast
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.util.Locale


@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    description: String?,
    imageUri: Uri?,
    viewModel: CameraScreenViewModel = viewModel()
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    if (locationPermissionState.status.isGranted) {
        Log.d("MapScreen", "Permiso de ubicación concedido")

        val cameraPositionState = rememberCameraPositionState()
        val context = LocalContext.current
        var userLocation by remember { mutableStateOf<LatLng?>(null) }
        val coroutineScope = rememberCoroutineScope()

        // Inicializamos el estado de description y de imageUri
        var descriptionState by rememberSaveable { mutableStateOf(description ?: "") }
        var imageUriState by rememberSaveable { mutableStateOf(imageUri?.toString() ?: "") }

        // Estado para el marcador
        val markerState = rememberMarkerState()

        // Lanzamos la corutina dentro de LaunchedEffect para obtener la ubicación
        LaunchedEffect(Unit) {
            try {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                val location = fusedLocationClient.lastLocation.await()

                location?.let {
                    userLocation = LatLng(it.latitude, it.longitude)
                    markerState.position = userLocation!!  // Actualizamos la posición del marcador
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(userLocation!!, 15f)
                    Log.d("MapScreen", "Posición de cámara y marcador establecida con la ubicación actual")
                } ?: run {
                    Log.e("MapScreen", "Ubicación no disponible")
                }

            } catch (e: Exception) {
                Log.e("MapScreen", "Error al obtener la ubicación actual: ${e.localizedMessage}")
            }
        }

        // Mostrar el mapa con el marcador en la ubicación actual
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Crear el marcador con el estado de posición
            Marker(
                state = markerState,
                title = "Ubicación actual",
                snippet = markerState.position.toString()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        // Botón para obtener la dirección de la ubicación actual
        Button(
            onClick = {
                coroutineScope.launch {
                    userLocation?.let { location ->
                        try {
                            val geocoder = Geocoder(context, Locale.getDefault())
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (addresses != null) {
                                if (addresses.isNotEmpty()) {
                                    val address = addresses[0]?.getAddressLine(0)
                                    Toast.makeText(context, "Dirección: $address", Toast.LENGTH_LONG).show()
                                    // Validar y codificar imageUriState antes de navegar
                                    val encodedImageUri = Uri.encode(imageUriState)
                                    val encodedAddress = Uri.encode(address.toString())

                                    navController.navigate("ReportScreen/$descriptionState/$encodedAddress/$encodedImageUri")



                                } else {
                                    Toast.makeText(context, "No se pudo obtener la dirección.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("MapScreen", "Error al obtener la dirección: ${e.localizedMessage}")
                            Toast.makeText(context, "Error al obtener la dirección.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Obtener Dirección")
        }
    } else {
        Log.d("MapScreen", "Permiso de ubicación denegado o no solicitado")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Se necesita permiso de ubicación para acceder al mapa.")
            Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                Text("Solicitar Permiso")
            }
        }
    }
}


