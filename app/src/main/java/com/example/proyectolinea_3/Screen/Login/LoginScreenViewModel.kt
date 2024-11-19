package com.example.proyectolinea_3.Screen.Login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginScreenViewModel : ViewModel() {

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

    fun singInWithEmailAndPassword(email: String, password: String) {
        _loading.value = true // Iniciar la carga

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        _loading.value = false // Finalizar la carga
                        if (task.isSuccessful) {
                            Log.d("Login", "Logueado")
                            _isAuthenticated.value = true // Actualizar estado de autenticación
                        } else {
                            _errorMessage.value = task.exception?.localizedMessage
                            Log.d("Login", "Fallo: ${_errorMessage.value}")
                        }
                    }
            } catch (ex: Exception) {
                _loading.value = false // Finalizar la carga
                _errorMessage.value = ex.message // Captura la excepción
                Log.d("Login", "Fallo: ${ex.message}")
            }
        }
    }

    // Resetea el mensaje de error
    fun resetErrorMessage() {
        _errorMessage.value = null
    }


}
