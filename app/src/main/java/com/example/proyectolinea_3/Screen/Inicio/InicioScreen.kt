package com.example.proyectolinea_3.Screen.Inicio

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Inicio(navController: NavController) {
    Scaffold(
        topBar = { MyTopBar(navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Contenedor de los botones en la pantalla principal
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                //verticalArrangement = Arrangement.Center,
                //horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainScreenButton(
                    icon = Icons.Default.LocationOn,
                    text = "",
                    style = MaterialTheme.typography.bodySmall,
                    onClick = { navController.navigate("MapaRutas") }
                )
                Spacer(modifier = Modifier.height(16.dp))
                MainScreenButton(
                    icon = Icons.Default.Email,
                    text = "",
                    onClick = { navController.navigate("ListaReportes") },
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                MainScreenButton(
                    icon = Icons.Default.ThumbUp,
                    text = "",
                    onClick = { navController.navigate("Recoleccion") },
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(16.dp))
                MainScreenButton(
                    icon = Icons.Default.Settings,
                    text = "",
                    onClick = { navController.navigate("ListarSolicitudes") },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun MainScreenButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    style: TextStyle
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0x80B2FF66)),
       // shape = RectangleShape, // Establece la forma rectangular
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .width(90.dp) // Ancho del botón
            .height(90.dp) // Alto del botón
            .padding(8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color(0xFF008000),
            modifier = Modifier.size(32.dp) // Tamaño del icono
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color(0xFF008000))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(navController: NavController) {
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

@Preview(showBackground = true)
@Composable
fun MyScreenPreview() {
    val navController = rememberNavController()
    Inicio(navController)
}