package com.example.proyectolinea_3.Screen.Recoleccionscreen

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import com.example.proyectolinea_3.Screen.Inicio.MyTopBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Recoleccion(
    navController: NavController,
    viewModel: RecoleccionScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var fechaRecoleccion by remember { mutableStateOf("") }
    var volumenResiduos by remember { mutableStateOf("") }
    val loading by viewModel.loading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val context = LocalContext.current

    val zonas = remember { mutableStateListOf<String>() }
    val expandedZone = remember { mutableStateOf(false) }
    val selectedZone = remember { mutableStateOf("Selecciona una Zona") }

    val residuos = remember { mutableStateListOf<String>() }
    val expandedResiduos = remember { mutableStateOf(false) }
    val selectedResiduos = remember { mutableStateOf("Selecciona un tipo de residuo") }

    LaunchedEffect(key1 = Unit) {
        try {
            val zonasData = viewModel.getZonesFromFirebase()
            Log.d("MapWithRoutes", "Zonas obtenidas: $zonasData")
            zonas.addAll(zonasData)
        } catch (e: Exception) {
            Log.e("MapWithRoutes", "Error obteniendo zonas: ${e.message}")
        }
    }

    LaunchedEffect(key1 = Unit) {
        try {
            val residuosData = viewModel.getTipoResiduoFromFirebase()
            Log.d("MapWithRoutes", "Residuos obtenidos: $residuosData")
            residuos.addAll(residuosData)
        } catch (e: Exception) {
            Log.e("MapWithRoutes", "Error obteniendo Tipos residuos: ${e.message}")
        }
    }

    Scaffold(
        topBar = { MyTopBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Campos de entrada y botón
                item {
                    // Dropdown para Zona
                    OutlinedTextField(
                        value = selectedZone.value,
                        onValueChange = { /* No se actualiza el valor directamente */ },
                        label = { Text("Zona") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable(onClick = { expandedZone.value = !expandedZone.value }) // Añadido clic para abrir el menú
                        ,
                        readOnly = true, // Hace que el campo no sea editable directamente
                        trailingIcon = {
                            IconButton(onClick = { expandedZone.value = !expandedZone.value }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown icon")
                            }
                        }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        // Mostrar el DropdownMenu
                        DropdownMenu(
                            expanded = expandedZone.value,
                            onDismissRequest = { expandedZone.value = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(1f) // Asegura que el DropdownMenu se renderice encima de otros componentes
                                .background(Color.White) // Asegura que tenga un fondo blanco (opcional, para mejorar visibilidad)
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

                    // Dropdown para Tipo de Residuo
                    OutlinedTextField(
                        value = selectedResiduos.value,
                        onValueChange = { /* No se actualiza el valor directamente */ },
                        label = { Text("Tipo de Residuo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable(onClick = { expandedResiduos.value = !expandedResiduos.value }) // Añadido clic para abrir el menú
                        ,
                        readOnly = true, // Hace que el campo no sea editable directamente
                        trailingIcon = {
                            IconButton(onClick = { expandedResiduos.value = !expandedResiduos.value }) {
                                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown icon")
                            }
                        }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        // Mostrar el DropdownMenu
                        DropdownMenu(
                            expanded = expandedResiduos.value,
                            onDismissRequest = { expandedResiduos.value = false },
                            modifier = Modifier
                                .fillMaxWidth()
                                .zIndex(1f) // Asegura que el DropdownMenu se renderice encima de otros componentes
                                .background(Color.White) // Asegura que tenga un fondo blanco (opcional, para mejorar visibilidad)
                        ) {
                            residuos.forEach { item ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedResiduos.value = item
                                        expandedResiduos.value = false
                                    },
                                    text = {
                                        Text(text = item, fontSize = 16.sp)
                                    }
                                )
                            }
                        }
                    }

                    TextField(
                        value = fechaRecoleccion,
                        onValueChange = { fechaRecoleccion = it },
                        label = { Text("Fecha de Recolección") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    TextField(
                        value = volumenResiduos,
                        onValueChange = { volumenResiduos = it },
                        label = { Text("Volumen de Residuos") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )

                    Button(
                        onClick = {
                            if (selectedZone.value.isNotEmpty() && selectedResiduos.value.isNotEmpty() &&
                                fechaRecoleccion.isNotEmpty() && volumenResiduos.isNotEmpty()
                            ) {
                                viewModel.solicitudRecoleccion(
                                    Zona = zonas.toString(),
                                    TipoResiduo = residuos.toString(),
                                    fecha_recoleccion = fechaRecoleccion,
                                    volumen_residuos = volumenResiduos
                                ) {
                                    selectedZone.value = ""
                                    selectedResiduos.value = ""
                                    fechaRecoleccion = ""
                                    volumenResiduos = ""

                                    Toast.makeText(context, "Solicitud enviada", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, "Error. Complete todos los campos", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Text("Enviar Solicitud")
                    }

                    if (loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    }
                    errorMessage?.let {
                        Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp))
                    }
                }

                // Lista de solicitudes
                item {
                    Button(onClick = { navController.navigate("ListarSolicitudes") }) {
                        Text(
                            text = "Solicitudes enviadas",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

