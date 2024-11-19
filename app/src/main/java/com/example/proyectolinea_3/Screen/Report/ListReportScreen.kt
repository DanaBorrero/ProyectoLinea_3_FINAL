package com.example.proyectolinea_3.Screen.Report

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.proyectolinea_3.Models.Reporte
import com.google.firebase.firestore.FirebaseFirestore
import coil.compose.AsyncImage
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.bumptech.glide.Glide
import com.example.proyectolinea_3.R
import com.example.proyectolinea_3.Screen.Inicio.MyTopBar
import com.example.proyectolinea_3.Screen.Report.utilities.CameraScreenViewModel
import com.example.proyectolinea_3.Screen.Report.utilities.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.File


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListReportScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val reportes = remember { mutableStateListOf<Reporte>() }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Cargar los reportes desde Firestore

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
                        contentDescription = "home"
                    )
                }
            }
        }
    )



    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid  // Obtiene el uid del usuario logeado

            db.collection("Reportes")
                .get()
                .addOnSuccessListener { result ->
                    reportes.clear()

                    for (document in result) {
                        val reporte = document.toObject(Reporte::class.java)
                        reportes.add(reporte)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("ListarReportes", "Error getting documents: ", exception)
                }
        } else {
            Log.w("ListarReportes", "Usuario no logeado")
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row() {
                        Button(onClick = {
                            navController.navigate("Reporte")
                        }, modifier = Modifier.padding(32.dp)) {
                            Text("Crear reporte")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                    }
                }

                items(reportes) { reporte ->
                    ReporteItem(
                        navController,
                        reporte,
                        sharedViewModel
                    ) // Pasar sharedViewModel aquí
                }

                item {
                    Spacer(modifier = Modifier.height(45.dp))
                }
            }
        }
    }
}


@Composable
fun ReporteItem(
    navController: NavController,
    reporte: Reporte,
    sharedViewModel: SharedViewModel // Recibir el sharedViewModel
) {
    val context = LocalContext.current

    // Obtener el archivo desde la ruta
    val filePath = reporte.imagen
    val file = filePath?.let { File(it) }

    // Generar la URI para Glide si el archivo existe
    val imageUri = if (file != null && file.exists()) {
        FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    } else {
        null
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = {
            sharedViewModel.selectedReporte = reporte // Guardar el reporte seleccionado en el ViewModel
            navController.navigate("EditarReporte")
        }
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Imagen a la izquierda
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(imageUri)
                            .placeholder(R.drawable.baseline_recycling_24)
                            .error(R.drawable.login_background)
                            .build()
                    ),
                    contentDescription = "Imagen del reporte",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Imagen predeterminada si no se encuentra el archivo
                Image(
                    painter = painterResource(R.drawable.baseline_recycling_24),
                    contentDescription = "Imagen predeterminada",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp)) // Separación entre la imagen y los textos

            // Contenido textual a la derecha
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = reporte.descripcion,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 3, // Limitar líneas
                    overflow = TextOverflow.Ellipsis, // Mostrar puntos suspensivos si excede
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Ubicación: ${reporte.ubicacion}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }

}












