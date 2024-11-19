package com.example.proyectolinea_3.Screen.Report

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import com.example.proyectolinea_3.Models.Reporte
import com.example.proyectolinea_3.R
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.layout.ContentScale
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.proyectolinea_3.Screen.Report.utilities.CameraComposable
import com.example.proyectolinea_3.Screen.Report.utilities.CameraScreenViewModel
import com.example.proyectolinea_3.Screen.Report.utilities.ImagePreview
import com.example.proyectolinea_3.Screen.Report.utilities.SharedViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun EditarEliminarReporteScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    var reporte = sharedViewModel.selectedReporte

    // Crear estados mutables para los campos editables
    var ubicacion by remember { mutableStateOf(reporte?.ubicacion ?: "") }
    var descripcion by remember { mutableStateOf(reporte?.descripcion ?: "") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var imagenFile by remember { mutableStateOf(reporte?.imagen ?: "") }
    var reporteId by remember { mutableStateOf(reporte?.reporteId ?: "") }
    var fecha by remember { mutableStateOf(reporte?.fecha ?: "") }
    var userId by remember { mutableStateOf(reporte?.userId ?: "") }


    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = android.Manifest.permission.CAMERA)

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { isSuccess ->
            if (isSuccess && imagenUri != null) {
                // Si la foto fue tomada correctamente, actualizamos la imagen en el reporte
                imagenUri?.let { uri ->
                    reporte?.imagen = uri.toString()
                }
            }
        }
    )

    // Obtener la URI para almacenar la imagen
    val photoUri = remember { Uri.fromFile(File(context.cacheDir, "reporte_imagen.jpg")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Mostrar la imagen si existe
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Gray, RoundedCornerShape(16.dp))
                ) {
                    if (imagenUri != null) {
                        // Si el usuario ha capturado una imagen, mostrarla
                        Image(
                            painter = rememberImagePainter(imagenUri),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Si no se ha capturado una foto, mostrar la imagen del reporte
                        Image(
                            painter = rememberImagePainter(reporte?.imagen),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = ubicacion,
                    onValueChange = { ubicacion = it },
                    label = { Text("Ubicación") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = { Text("Ubicación") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                // Botones para guardar, eliminar y tomar una nueva foto
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = {
                        // Asegúrate de que 'reporte' no sea nulo antes de pasarlo a updateReport
                        reporte?.let {
                            sharedViewModel.updateReport(
                                reporteId = reporteId,
                                descripcion = descripcion,
                                fecha = fecha,
                                imagenUri = imagenFile,
                                ubicacion = ubicacion,
                                userId = userId
                            ) { success ->
                                if (success) {
                                    Toast.makeText(context, "Reporte actualizado correctamente", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al actualizar el reporte", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }) {
                        Text("Guardar")
                    }
                    Button(onClick = {
                        reporte?.let {
                            sharedViewModel.deleteReport(reporteId, onComplete = {
                                if (it) {
                                    Toast.makeText(context, "Reporte eliminado correctamente", Toast.LENGTH_SHORT).show()
                                    navController.navigate("Reporte")
                                }else{
                                    Toast.makeText(context, "Error al eliminar el reporte", Toast.LENGTH_SHORT).show()
                                }
                            })
                        }
                    }) {
                        Text("Eliminar")
                    }
//                    Button(onClick = {
//                        // Verifica si el permiso de cámara está concedido cuando el usuario intenta tomar la foto
//                        if (permissionState.status.isGranted) {
//                            takePictureLauncher.launch(photoUri)
//                        } else {
//                            permissionState.launchPermissionRequest()
//                        }
//                    }) {
//                        Text("Tomar Foto")
//                    }
                }
            }
        }
    }
}







