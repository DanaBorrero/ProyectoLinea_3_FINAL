package com.example.proyectolinea_3.Screen.Report.utilities

import android.Manifest
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.util.concurrent.Executor


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermission(
    navController: NavController,
    description: String?,
    location: String?,
    viewModel: CameraScreenViewModel = viewModel()
) {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showPreview by remember { mutableStateOf(false) }

    // Archivo de imagen predefinido
    val context = LocalContext.current
    val imageFile = remember { File(context.cacheDir, "captured_image.jpg") }

    val lifecycle = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context) }
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val storagePermissionState = rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if (cameraPermissionState.status.isGranted && storagePermissionState.status.isGranted && !showPreview) {
                FloatingActionButton(onClick = {
                    // Captura la imagen en el archivo predefinido
                    viewModel.TakePicture(
                        cameraController = cameraController,
                        executor = ContextCompat.getMainExecutor(context),
                        context = context,
                        imageFile = imageFile, // Pasa el archivo
                        onImageCaptured = { uri ->
                            imageUri = uri
                            showPreview = true
                        }
                    )
                }) {
                    Icon(Icons.Default.Person, contentDescription = "Capturar Imagen")
                }
            }
        }
    ) {
        if (cameraPermissionState.status.isGranted && storagePermissionState.status.isGranted) {
            if (!showPreview) {
                CameraComposable(
                    cameraController = cameraController,
                    lifecycle = lifecycle,
                    modifier = Modifier.padding(it)
                )
            } else if (imageUri != null) {
                ImagePreview(
                    imageUri = imageUri,
                    onSave = { context ->
                        val originalFile = File(imageUri?.path ?: "")

                        // Duplica el archivo con un nuevo nombre
                        val newUri = viewModel.duplicateImageFile(
                            context = context,
                            originalFile = originalFile,
                            newFileName = "image_${System.currentTimeMillis()}.jpg"
                        )

                        if (newUri != null) {
                            viewModel.capturedImage = newUri // Actualiza la nueva URI en el ViewModel

                            // Aquí pasas el archivo duplicado al Firebase
                            val encodedImageUri = Uri.encode(newUri.toString())
                            val encodedLocation = Uri.encode(location)
                            val encodedDescription = Uri.encode(description)

                            // Navega con la nueva URI del archivo duplicado
                            navController.navigate("ReportScreen/$encodedDescription/$encodedLocation/$encodedImageUri")
                        } else {
                            Toast.makeText(context, "Error al duplicar la imagen", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onDiscard = {
                        showPreview = false
                        imageUri = null
                    }
                )
            }
        }
    }
}


@Composable
fun CameraComposable(
    cameraController: LifecycleCameraController,
    lifecycle: LifecycleOwner,
    modifier: Modifier = Modifier
) {
    cameraController.bindToLifecycle(lifecycle)
    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            previewView.controller = cameraController
            previewView
        }
    )
}

@Composable
fun ImagePreview(imageUri: Uri?, onSave: (Context) -> Unit, onDiscard: () -> Unit) {
    val context = LocalContext.current

    imageUri?.let {
        val bitmap = remember(imageUri) {
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(it))
        }
        val imageBitmap = bitmap.asImageBitmap()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                bitmap = imageBitmap,
                contentDescription = "Imagen Capturada",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { onSave(context) }) { // Pasa el contexto al callback
                    Icon(Icons.Default.Done, contentDescription = "Guardar")
                }
                IconButton(onClick = onDiscard) {
                    Icon(Icons.Default.Refresh, contentDescription = "Descartar")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

