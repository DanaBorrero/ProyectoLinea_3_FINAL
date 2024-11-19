package com.example.proyectolinea_3.Screen.Register

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectolinea_3.R
import com.example.proyectolinea_3.Screen.Login.LoginScreenViewModel
import kotlinx.coroutines.withContext
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Register(
    navController: NavController,
    viewModel: RegisterScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var lastName by remember { mutableStateOf(TextFieldValue("")) }
    var email by remember { mutableStateOf(TextFieldValue("")) }
    var password by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf(TextFieldValue("")) }

    val zonas = remember { mutableStateListOf<String>() }
    val selectedZone = remember { mutableStateOf("Selecciona una Zona") }
    val expandedZone = remember { mutableStateOf(false) }


    val loading by viewModel.loading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        try {
            val zonasData = viewModel.getZonesFromFirebase()
            Log.d("MapWithRoutes", "Zonas obtenidas: $zonasData")
            zonas.addAll(zonasData)
        } catch (e: Exception) {
            Log.e("MapWithRoutes", "Error obteniendo zonas: ${e.message}")
        }
    }


    // Fondo de la pantalla
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.login_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Registro",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Campos de entrada
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apellido") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Electrónico") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Text(if (passwordVisible) "Ocultar" else "Mostrar")
                        }
                    }
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Celular") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp), // Espaciado inferior
                    singleLine = true
                )

                OutlinedTextField(
                    value = selectedZone.value,
                    onValueChange = { /* No se actualiza el valor directamente */ },
                    label = { Text("Zona") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp) // Igual al OutlinedTextField de teléfono
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



                // Botón de Registro
                Button(
                    onClick = {
                        if (name.text.isEmpty() || lastName.text.isEmpty() || email.text.isEmpty() || password.text.isEmpty() || phone.text.isEmpty() || selectedZone.value.isEmpty()) {
                            Toast.makeText(context, "Por favor ingrese todos los campos", Toast.LENGTH_SHORT).show()
                        } else {
                            // Llama al ViewModel para iniciar el registro
                            viewModel.createUserWithEmailAndPassword(name.text, lastName.text, email.text, password.text, phone.text, selectedZone.value) { success ->
                                if (success) {
                                    // Navegar al Login después de un registro exitoso
                                    navController.popBackStack("Register", inclusive = true)
                                    navController.navigate("Login")
                                } else {
                                    // Muestra un mensaje de error si el registro falla
                                    Toast.makeText(context, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text(text = "Registrarse")
                }

            }
        }

        // Mostrar Snackbar si hay un mensaje de error
        errorMessage?.let {
            Snackbar(
                action = {
                    Button(onClick = { viewModel.resetErrorMessage() }) {
                        Text("Cerrar")
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text(it)
            }
        }
    }
}
