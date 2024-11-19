package com.example.proyectolinea_3.Screen.Horario

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.tasks.await

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.zIndex
import com.example.proyectolinea_3.Screen.Inicio.MainScreenButton
import com.example.proyectolinea_3.Screen.Inicio.MyTopBar
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapWithRoutes(
    navController: NavController,
    viewModel: HorarioRecoleccionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    // Estado para las coordenadas de la ruta seleccionada
    val selectedRoute = remember { mutableStateOf("Selecciona una Ruta") }
    val selectedRouteMap = remember { mutableStateOf<Map<String, Any>?>(null) }
    val selectedZone = remember { mutableStateOf("Selecciona una Zona") } // Valor predeterminado claro

    val coordinates = remember { mutableStateOf<Pair<String, String>?>(null) }
    val directions = remember { mutableStateOf<Pair<String, String>?>(null) }

    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val cameraPositionState = rememberCameraPositionState()

    val localidades = remember { mutableStateListOf<Map<String, Any>>() } // Para usar fuera de Compose
    val zonas = remember { mutableStateListOf<String>() }  // Nuevo estado para las zonas

    val expandedRoute = remember { mutableStateOf(false) }
    val expandedZone = remember { mutableStateOf(false) }  // Nuevo estado para el dropdown de zonas

    val isLoadingZones = remember { mutableStateOf(true) } // Indicador de carga de zonas
    val isLoadingRoutes = remember { mutableStateOf(true) } // Indicador de carga de localidades


    val registro = remember {
        mutableStateOf<Map<String, String>?>(null)
    }
    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
    }


    // Obtener las zonas desde Firebase
    LaunchedEffect(key1 = Unit) {
        try {
            val zonasData = viewModel.getZonesFromFirebase()
            Log.d("MapWithRoutes", "Zonas obtenidas: $zonasData")
            zonas.addAll(zonasData)
        } catch (e: Exception) {
            Log.e("MapWithRoutes", "Error obteniendo zonas: ${e.message}")
        }
    }

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    TopAppBar(
        title = {
            if (isSearching) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar...") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors()
                )

            } else {
                Text(
                    text = "GreenWay",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0x80B2FF66),
            titleContentColor = Color(0xFF008000),
        ),
        actions = {
            if (isSearching) {
                IconButton(onClick = {
                    isSearching = false
                    searchQuery = "" // Limpiar el texto de búsqueda
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar búsqueda"
                    )
                }
            } else {
                IconButton(onClick = { isSearching = true }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                }
                IconButton(onClick = {
                    navController.navigate("home") // Navegar a la pantalla de inicio
                }) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Inicio"
                    )
                }
            }
        }
    )
    Scaffold(
        topBar = { MyTopBar(navController) }
    ) { paddingValues ->
    // Mostrar el mapa solo si las coordenadas están disponibles
    Box(
        modifier = Modifier.fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedZone.value = !expandedZone.value }
                    .background(Color(0x722196F3), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(text = selectedZone.value.ifEmpty { "Selecciona una Zona" })


                DropdownMenu(
                    expanded = expandedZone.value,
                    onDismissRequest = { expandedZone.value = false },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .zIndex(2f) // Asegura que el DropdownMenu se renderice por encima del mapa
                ) {
                    zonas.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                selectedZone.value = item
                                expandedZone.value = false
                            },
                            text = {
                                Text(text = item, fontSize = 16.sp)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Carga de rutas al seleccionar una zona
            LaunchedEffect(selectedZone.value) {
                if (selectedZone.value.isEmpty()) {
                    Log.e("MapWithRoutes", "Zona seleccionada está vacía o nula.")
                    return@LaunchedEffect
                }
                try {
                    val localidadesData = viewModel.getLocalidadesByZone(selectedZone.value)
                    localidades.clear()
                    localidades.addAll(localidadesData)
                    Log.d("MapWithRoutes", "Localidades del LaunchedEffect: $localidades")
                } catch (e: Exception) {
                    Log.e("MapWithRoutes", "Error en LaunchedEffect: ${e.message}")
                }
            }
            Log.d("MapWithRoutes", "Localidades obtenidas: $localidades")
            // Menú desplegable de localidades
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedRoute.value = !expandedRoute.value }
                    .background(Color(0x722196F3), shape = RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(text = selectedRoute.value.ifEmpty { "Selecciona una Ruta" })

                DropdownMenu(
                    expanded = expandedRoute.value,
                    onDismissRequest = { expandedRoute.value = false },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .zIndex(1f)
                ) {
                    localidades.forEach { localidad ->
                        val localidadName = localidad["SectorCubierto"] as? String ?: "Sin nombre"
                        DropdownMenuItem(
                            onClick = {
                                selectedRoute.value = localidadName
                                selectedRouteMap.value = localidad // Guarda la localidad completa
                                expandedRoute.value = false
                            },
                            text = {
                                Text(text = localidadName, fontSize = 16.sp)
                            }
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(16.dp))


            Log.d("MapWithRoutes", "Ruta seleccionada: ${selectedRouteMap.value}")
            Log.d("MapWithRoutes", "Zona seleccionada: ${selectedZone.value}")

            LaunchedEffect(selectedRouteMap.value, selectedZone.value) {
                if (selectedRouteMap.value != null) {
                    val querySnapshot =
                        viewModel.getCoordinatesForRoute(selectedRouteMap.value, selectedZone.value)
                            .await()

                    Log.d("MapWithRoutes", "QuerySnapshot: $querySnapshot")

                    querySnapshot?.documents?.firstOrNull()?.let { document ->
                        // Extraer los campos necesarios
                        val coordenadasInicio = document.getString("CoordenadasInicio") ?: ""
                        val coordenadasFinal = document.getString("CoordenadasFinal") ?: ""
                        val direccionInicio = document.getString("DireccionInicio") ?: ""
                        val direccionFinal = document.getString("DireccionFinal") ?: ""
                        val diasRecoleccion = document.getString("DiasRecoleccion") ?: ""
                        val jornada = document.getString("Jornada") ?: ""
                        val localidad = document.getString("Localidad") ?: ""
                        val sectorCubierto = document.getString("SectorCubierto") ?: ""

                        if (coordenadasInicio.isNotEmpty() && coordenadasFinal.isNotEmpty()) {
                            // Asignar valores al estado
                            coordinates.value = Pair(coordenadasInicio, coordenadasFinal)
                            directions.value = Pair(direccionInicio, direccionFinal)

                            Log.d("MapWithRoutes", "Coordenadas obtenidas: $coordinates")
                            Log.d("MapWithRoutes", "Direcciones obtenidas: $directions")

                            registro.value = mapOf(
                                "Coordenadas Inicio" to coordenadasInicio,
                                "Coordenadas Final" to coordenadasFinal,
                                "Dirección Inicio" to direccionInicio,
                                "Dirección Final" to direccionFinal,
                                "Días Recolección" to diasRecoleccion,
                                "Jornada" to jornada,
                                "Localidad" to localidad,
                                "Sector Cubierto" to sectorCubierto
                            )
                        } else {
                            Log.e("MapWithRoutes","No se encontraron coordenadas para la ruta seleccionada.")
                        }
                    }
                }
            }

            val originCoordinates = coordinates.value?.first ?: ""
            val destinationCoordinates = coordinates.value?.second ?: ""

            if (viewModel.isLoading.value) {
                Text(
                    text = "Cargando localidades...",
                    modifier = Modifier.padding(top = 8.dp)
                )
            } else {


                // Contenedor del mapa
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.5f)
                        .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                        .zIndex(0f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        // Mostrar el mapa
                        if (originCoordinates.isNotEmpty() && destinationCoordinates.isNotEmpty()) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState
                            ) {
                                val originCoords = originCoordinates.split(",")
                                val destinationCoords = destinationCoordinates.split(",")

                                if (originCoords.size == 2 && destinationCoords.size == 2) {
                                    val originLat = originCoords[0].toDouble()
                                    val originLng = originCoords[1].toDouble()
                                    val destinationLat = destinationCoords[0].toDouble()
                                    val destinationLng = destinationCoords[1].toDouble()

                                    val originMarkerState = remember {
                                        MarkerState(position = LatLng(originLat, originLng))
                                    }
                                    val destinationMarkerState = remember {
                                        MarkerState(
                                            position = LatLng(
                                                destinationLat,
                                                destinationLng
                                            )
                                        )
                                    }

                                    // Centrar la cámara entre los dos puntos
                                    cameraPositionState.position = CameraPosition.fromLatLngZoom(
                                        LatLng(
                                            (originLat + destinationLat) / 2,
                                            (originLng + destinationLng) / 2
                                        ),
                                        12f
                                    )

                                    // Marcadores
                                    Marker(
                                        state = originMarkerState,
                                        title = "Origen",
                                        snippet = directions.value?.first ?: "Cargando dirección..."
                                    )
                                    Marker(
                                        state = destinationMarkerState,
                                        title = "Destino",
                                        snippet = directions.value?.second
                                            ?: "Cargando dirección..."
                                    )
                                }
                            }
                        } else {
                            // Mostrar un indicador de carga si no hay coordenadas
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Tabla con datos del registro
                registro.value?.let { data ->
                    Text("Detalles del Registro", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            .padding(8.dp)
                            .fillMaxHeight() // Asegura que la LazyColumn tenga una altura definida
                    ) {
                        data.forEach { (key, value) ->
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = key,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = value,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                                Divider(color = Color.Gray, thickness = 0.5.dp)
                            }
                        }
                    }

                }

            }

        }
        }
    }
}









