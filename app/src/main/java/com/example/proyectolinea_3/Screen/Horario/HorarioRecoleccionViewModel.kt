package com.example.proyectolinea_3.Screen.Horario

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await  // Importación necesaria

class HorarioRecoleccionViewModel : ViewModel() {

    private val _zones = mutableStateOf<List<String>>(emptyList())
    val zones: State<List<String>> = _zones

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading

    private val _localities = mutableStateOf<List<String>>(emptyList())
    val localities: State<List<String>> = _localities

    // Obtener zonas desde Firebase

    suspend fun getZonesFromFirebase(): List<String> {
        val zonesList = mutableListOf<String>()
        try {
            // Realiza la consulta asincrónica y espera a que termine con await
            val zonesSnapshot = Firebase.firestore.collection("Zona").get().await()

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


// Obtener localidades de la zona seleccionada
suspend fun getLocalidadesByZone(zone: String): List<Map<String, Any>> {
    if (zone.isEmpty()) throw IllegalArgumentException("Zona no puede ser vacía")

    Log.d("Firestore", "Obteniendo localidades para la zona: $zone")
    val localidades = mutableListOf<Map<String, Any>>()
    val documentReference = FirebaseFirestore.getInstance()
        .collection("Zona")
        .document(zone)

    val documentSnapshot = documentReference.get().await()
    if (!documentSnapshot.exists()) {
        Log.e("Firestore", "El documento $zone no existe en la colección Zonas")
        return emptyList()
    }


    try {
        val snapshot = documentReference.collection("Localidades").get().await()
        for (doc in snapshot.documents) {
            localidades.add(doc.data ?: emptyMap())
        }
        Log.d("Firestore", "Localidades obtenidas: $localidades")
    } catch (e: Exception) {
        Log.e("Firestore", "Error obteniendo localidades: ${e.message}")
    }

    return localidades
}




    suspend fun getCoordinatesForRoute(selectedRoute: Map<String, Any>?, selectedZone: String) =
        FirebaseFirestore.getInstance()
            .collection("Zona")
            .document(selectedZone)
            .collection("Localidades")
            .whereEqualTo("DireccionInicio", selectedRoute?.get("DireccionInicio") ?: "")
            .get()


}



