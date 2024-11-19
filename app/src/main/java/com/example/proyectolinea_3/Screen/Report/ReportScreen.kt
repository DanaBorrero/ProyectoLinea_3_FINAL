package com.example.proyectolinea_3.Screen.Report

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.proyectolinea_3.Models.Reporte
import com.example.proyectolinea_3.Screen.Report.utilities.CameraScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun ReportScreen(
    navController: NavController,
    description: String?,
    location: String?,
    imageUri: Uri?,
    viewModel: CameraScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    var descriptionState by remember { mutableStateOf(description ?: "") }
    //val decodedAddress = Uri.decode(location)
    var locationState by remember { mutableStateOf(location ?: "") }
    val imageUriState by remember { mutableStateOf(imageUri?.toString() ?: "") }
    val encodedImageUri = Uri.encode(imageUriState)

    val bitmap = remember(imageUri) { viewModel.loadBitmapFromUri(context, imageUri) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton (
            onClick = {
                navController.navigate("ListaReportes")
            },
            modifier = Modifier.size(50.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Icono de Reporte"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if(!location.isNullOrEmpty()) {
                    OutlinedTextField(
                        value = locationState,
                        onValueChange = { locationState = it },
                        label = { Text("Ubicación") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    )
                }
            }

            item {
                OutlinedTextField(
                    value = descriptionState,
                    onValueChange = { descriptionState = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Button(
                    onClick = {
                            try {
                                navController.navigate("Map/$descriptionState/$encodedImageUri")
                            } catch (e: Exception) {
                                Log.e("ReportScreen", "Error al navegar al mapa: ${e.localizedMessage}")
                                Toast.makeText(context, "Error al abrir el mapa", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Obtener Ubicación")
                }

            }


            item {
                Button(
                    onClick = {
                        navController.navigate("Camara/$descriptionState/$locationState")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Capturar Imagen")
                }
            }

            item {
                Button(
                    onClick = {
                        if (descriptionState.isNotEmpty() && imageUriState.isNotEmpty() && locationState.isNotEmpty()) {

                        val reporte = Reporte(
                            reporteId = UUID.randomUUID().toString(),
                            userId = currentUser?.uid.toString(),
                            descripcion = descriptionState,
                            fecha = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(Date()),
                            ubicacion = locationState,
                            imagen = imageUri?.toString()
                        )
                        viewModel.uploadImageAndSaveReport(context, reporte) { success ->
                            if (success) {
                                Toast.makeText(context,"Reporte guardado exitosamente",Toast.LENGTH_SHORT).show()
                                navController.navigate("Reporte") {
                                    popUpTo("Camara") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context,"Error al guardar el reporte",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                            Toast.makeText(context,"Rellene todos los campos",Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Reporte")
                }
            }
            item {
                bitmap?.let {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // Ancho completo
                            .height(350.dp) // Altura mayor
                            .clip(RoundedCornerShape(16.dp)) // Esquinas redondeadas
                            .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)) // Borde gris
                    ) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop, // Ajusta la imagen para que se recorte adecuadamente
                            modifier = Modifier.fillMaxSize() // Hace que la imagen ocupe todo el Box
                        )
                    }


                } ?: run {
                    Text(
                        "No se ha cargado una imagen",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

