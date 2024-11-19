package com.example.proyectolinea_3.Screen.Recoleccionscreen

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex


@Composable
fun SolicitudItem1(solicitud: Map<String, Any>
                  , viewModel: RecoleccionScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    // Extraer datos de la solicitud
    val zona = solicitud["Zona"] as? String ?: "Desconocido"
    val tipoResiduo = solicitud["TipoResiduo"] as? String ?: "Desconocido"
    val fecha = solicitud["fecha_recoleccion"] as? String ?: "Sin fecha"
    val volumen = solicitud["volumen_residuos"] as? String ?: "Sin volumen"
    val solicitudId = solicitud["id"] as? String ?: ""
    val context = LocalContext.current

    var isEditDialogOpen by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp), // Espaciado entre tarjetas
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Relleno interno de la tarjeta
        ) {
            Text(text = "Zona: $zona", style = MaterialTheme.typography.bodyLarge)
            Text(text = "Tipo de residuo: $tipoResiduo", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha de recolección: $fecha", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Volumen de residuos: $volumen", style = MaterialTheme.typography.bodyMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón de Editar
                Button(onClick = { isEditDialogOpen = true }) {
                    Text("Editar")
                }

                // Botón de Eliminar
                Button(onClick = {
                    viewModel.deleteSolicitud(solicitudId, onComplete = {
                        if (it) {
                            Toast.makeText(
                                context,
                                "Solicitud eliminada correctamente",
                                Toast.LENGTH_SHORT
                                ).show()
                        } else {
                            Toast.makeText(context,"Error al eliminar la solicitud", Toast.LENGTH_SHORT).show()
                        }
                    })

                }) {
                    Text("Eliminar")
                }
            }
        }
    }


    if (isEditDialogOpen) {
        EditDialog(
            solicitudId = solicitudId,
            zona = zona,
            tipoResiduo = tipoResiduo,
            fecha = fecha,
            volumen = volumen,
            onDismiss = { isEditDialogOpen = false },
            onSave =
            { succes ->
                // Actualizar la solicitud con los datos nuevos
                viewModel.updateSolicitud(
                    id = solicitudId,
                    zona = zona,
                    tipoResiduo = tipoResiduo,
                    fechaRecoleccion = fecha,
                    volumenResiduos = volumen,
                    onComplete =
                    {
                        if (it) {
                            Toast.makeText(
                                context,
                                "Reporte actualizado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Error al actualizar el reporte",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })

                isEditDialogOpen = false
            }
        )
    }
}

@Composable
fun EditDialog1(
    solicitudId: String,
    zona: String,
    tipoResiduo: String,
    fecha: String,
    volumen: String,
    onDismiss: () -> Unit,
    onSave: (Map<String, Any>) -> Unit,
    viewModel: RecoleccionScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var newZona by remember { mutableStateOf(zona) }
    var newTipoResiduo by remember { mutableStateOf(tipoResiduo) }
    var newFecha by remember { mutableStateOf(fecha) }
    var newVolumen by remember { mutableStateOf(volumen) }

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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Solicitud") },
        text = {
            Column {
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
                    value = newFecha,
                    onValueChange = { newFecha = it },
                    label = { Text("Fecha") }
                )
                TextField(
                    value = newVolumen,
                    onValueChange = { newVolumen = it },
                    label = { Text("Volumen") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                // Guardar los cambios y enviar los datos actualizados
                val updatedData = mapOf(
                    "id" to solicitudId,
                    "Zona" to newZona,
                    "TipoResiduo" to newTipoResiduo,
                    "fecha_recoleccion" to newFecha,
                    "volumen_residuos" to newVolumen
                )
                onSave(updatedData)
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}



@Composable
fun ListaSolicitudesScreen(navController: NavController,
                           viewModel: RecoleccionScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val solicitudes by viewModel.solicitudes.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    Scaffold(
        topBar = { BarraSuperior12(navController) }
    ) { paddingValues ->


                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .padding(paddingValues)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                            .padding(32.dp)
                    ) {
                        if (loading) {
                            // Mostrar indicador de carga
                            CircularProgressIndicator()
                        } else if (errorMessage != null) {
                            // Mostrar mensaje de error
                            Text(text = "Error: $errorMessage")
                        } else {
                            // Mostrar la lista de solicitudes

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                viewModel.obtenerSolicitudes()
                                items(solicitudes) { solicitud ->
                                    SolicitudItem1(solicitud)
                                }
                            }
                        }
                    }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior12(navController: NavController) {
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
}


@Composable
fun ListaSolicitudesScreenPreview21() {
    val navController = rememberNavController()
    ListaSolicitudesScreen(navController, RecoleccionScreenViewModel())
}


