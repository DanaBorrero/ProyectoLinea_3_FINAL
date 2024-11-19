package com.example.proyectolinea_3.Screen.Register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectolinea_3.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterScreenViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    // LiveData para el estado de carga
    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    // LiveData para el mensaje de error
    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    // LiveData para el estado de autenticación
    private val _isAuthenticated = MutableLiveData<Boolean>(false)
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    fun createUserWithEmailAndPassword(
        nombre: String,
        apellido: String,
        email: String,
        password: String,
        celular: String,
        ubicacion: String,
        onResult: (Boolean) -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _loading.value = false
                    if (task.isSuccessful) {
                        _isAuthenticated.value = true // Actualiza el estado de autenticación
                        Log.e("RegisterSuccess", "Registro exitoso")
                        createUser(nombre, apellido, email, password, celular, ubicacion, onResult)
                        onResult(true) // Navega a la pantalla de Login
                    } else {
                        _errorMessage.value = task.exception?.localizedMessage
                        onResult(false)
                        Log.e("RegisterError", task.exception?.localizedMessage ?: "Error desconocido")
                    }
                }
        }
    }

    suspend fun getZonesFromFirebase(): List<String> {
        val zonesList = mutableListOf<String>()
        try {
            // Realiza la consulta asincrónica y espera a que termine con await
            val zonesSnapshot = com.google.firebase.Firebase.firestore.collection("Zona").get().await()

            // Itera sobre los documentos dentro del snapshot
            for (document in zonesSnapshot.documents) {
                val zoneName = document.id
                zonesList.add(zoneName)
            }
            Log.d("Firebase", "Zonas obtenidas: $zonesList")
        } catch (e: Exception) {
            Log.e("Firebase", "Error obteniendo zonas: ${e.message}")
        }
        return zonesList
    }

     private fun createUser(nombre: String, apellido: String, email: String, password: String, celular: String, ubicacion: String, onResult: (Boolean) -> Unit){

        val userId = Firebase.auth.currentUser?.uid

        val user = User(
            userId = userId.toString(),
            nombre = nombre.toString(),
            apellido = apellido.toString(),
            email = email.toString(),
            password = password.toString(),
            celular = celular.toString(),
            ubicacion = ubicacion.toString()
        ).toMap()

        FirebaseFirestore.getInstance().collection("Usuarios")
            .add(user)
            .addOnSuccessListener {
                Log.d("Add User", "Creado correctamente")
                onResult(true)
            }.addOnFailureListener {
                Log.d("Add User", "Fallo")
                onResult(false)
            }

    }


    // Resetea el mensaje de error
    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}

