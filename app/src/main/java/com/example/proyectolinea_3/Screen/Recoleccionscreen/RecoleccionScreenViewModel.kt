package com.example.proyectolinea_3.Screen.Recoleccionscreen

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectolinea_3.Models.Recoleccion
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RecoleccionScreenViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _solicitudes = MutableLiveData<List<Map<String, Any>>>()
    val solicitudes: LiveData<List<Map<String, Any>>> get() = _solicitudes

    private val _tiposResiduos = MutableLiveData<List<Map<String, Any>>>()
    val tiposResiduos: LiveData<List<Map<String, Any>>> get() = _tiposResiduos



    fun solicitudRecoleccion(
        Zona: String,
        TipoResiduo: String,
        fecha_recoleccion: String,
        volumen_residuos: String,
        onResult: (Boolean) -> Unit
    ) {
        if (_loading.value == false) {
            _loading.value = true

            val solicitudData = mapOf(
                "Zona" to Zona,
                "TipoResiduo" to TipoResiduo,
                "fecha_recoleccion" to fecha_recoleccion,
                "volumen_residuos" to volumen_residuos
            )

            db.collection("Recoleccion")
                .add(solicitudData)
                .addOnSuccessListener {
                    _loading.value = false
                    onResult(true)
                }
                .addOnFailureListener { e ->
                    _loading.value = false
                    _errorMessage.value = e.localizedMessage
                    onResult(false)
                }
        }
    }

    fun obtenerSolicitudes() {
        db.collection("Recoleccion")
            .get()
            .addOnSuccessListener { result ->
                val listaSolicitudes = result.map { it.data }
                _solicitudes.value = listaSolicitudes
            }
            .addOnFailureListener { e ->
                Log.e("Recoleccion", "Error al obtener solicitudes", e)
            }
    }

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

    suspend fun getTipoResiduoFromFirebase(): List<String> {
        val residuoList = mutableListOf<String>()
        try {
            // Realiza la consulta asincrónica y espera a que termine con await
            val residuoSnapshot = Firebase.firestore.collection("TipoResiduo").get().await()

            // Itera sobre los documentos dentro del snapshot
            for (document in residuoSnapshot.documents) {
                val residuoName = document.id
                residuoList.add(residuoName)
            }
            Log.d("Firebase", "Tipos de Residuos obtenidas: $residuoList")
        } catch (e: Exception) {
            Log.e("Firebase", "Error obteniendo Tipos de Residuo: ${e.message}")
        }
        return residuoList
    }

    fun deleteSolicitud(solicitudId: String, onComplete: (Boolean) -> Unit) {
        db.collection("Recoleccion")
            .document(solicitudId)
            .delete()
            .addOnSuccessListener {
                onComplete(true) // Éxito en la eliminación
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al eliminar el reporte", exception)
                onComplete(false) // Error al eliminar
            }
    }

    fun updateSolicitud(id: String, zona: String, tipoResiduo: String, fechaRecoleccion: String, volumenResiduos: String, onComplete: (Boolean) -> Unit) {

        val updatedData = Recoleccion( id, zona, tipoResiduo, fechaRecoleccion, volumenResiduos)

        db.collection("Recoleccion")
            .document(updatedData.id)
            .set(updatedData)
            .addOnSuccessListener {
                onComplete(true) // Éxito en la actualización
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al actualizar el reporte", exception)
                onComplete(false) // Error al actualizar
            }
    }

}


