package com.example.proyectolinea_3.Screen.Report.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.proyectolinea_3.Models.Reporte
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.type.LatLng
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executor


class CameraScreenViewModel : ViewModel() {

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String?>(null)
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _uploadSuccess = MutableLiveData<Boolean>(false)
    val uploadSuccess: LiveData<Boolean> get() = _uploadSuccess

    private val storage = FirebaseStorage.getInstance()
    private val db = FirebaseFirestore.getInstance()


    var userLocation: LatLng? = null

    var capturedImage by mutableStateOf<Uri?>(null)

    // Función para crear el archivo de imagen
    fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileName = "JPEG_${timeStamp}_"
        val imageFile = File.createTempFile(fileName, ".jpg", storageDir)

        if (imageFile.exists()) {
            Log.d("CameraScreen", "File created: ${imageFile.absolutePath}")
        } else {
            Log.e("CameraScreen", "File was not created.")
        }

        return imageFile
    }

    fun duplicateImageFile(
        context: Context,
        originalFile: File,
        newFileName: String
    ): Uri? {
        return try {
            // Crear un nuevo archivo en el mismo directorio
            val newFile = File(originalFile.parent, newFileName)

            // Copiar el contenido del archivo original al nuevo archivo
            originalFile.inputStream().use { inputStream ->
                newFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            // Retornar la URI del nuevo archivo
            Uri.fromFile(newFile)
        } catch (e: Exception) {
            Log.e("CameraPermission", "Error al duplicar el archivo: ${e.message}")
            null
        }
    }


    fun uploadImageAndSaveReport(context: Context, reporte: Reporte, onComplete: (Boolean) -> Unit) {
        _loading.value = true // Establecer loading en true al inicio

        // Verifica si la imagen existe en la ruta proporcionada
        var imageFile = reporte.imagen?.let { File(it) }

        // Eliminar el prefijo 'file:/' de la ruta si existe
        imageFile?.let {
            val newPath = it.absolutePath.replace("file:/", "")  // Aquí quitamos "file:/"
            imageFile = File(newPath) // Asigna la nueva ruta sin 'file:/'
        }

        if (imageFile != null && !imageFile!!.exists()) {
            Log.e("CameraScreen", "El archivo de imagen no se pudo crear o no existe: ${imageFile!!.absolutePath}")
            _errorMessage.value = "Error al crear el archivo de imagen."
            _loading.value = false
            onComplete(false)
            return
        }

        // Asigna la ruta absoluta del archivo al campo imagen del reporte
        if (imageFile != null) {
            reporte.imagen = imageFile!!.absolutePath
        } // Asegura que la URI local aquí sea correcta

        // Guarda el reporte en Firestore con la ruta local de la imagen
        saveReportToFirestore(reporte) { success ->
            _loading.value = false // Reset loading
            onComplete(success)
        }

        // Aquí puedes agregar un log para verificar la ruta
        if (imageFile != null) {
            Log.d("CameraScreen", "Ruta del archivo guardada en Firestore: ${imageFile!!.absolutePath}")
        }
    }




    private fun saveReportToFirestore(reporte: Reporte, onResult: (Boolean) -> Unit) {
        Log.d("Firestore", "Iniciando guardado de reporte en Firestore con datos: ${reporte.toMap()}")

        db.collection("Reportes")
            .document(reporte.reporteId) // Aquí especificamos el ID del documento
            .set(reporte.toMap().apply {
                put("imagen", reporte.imagen) // Asegura que la imagen se guarde
            })
            .addOnSuccessListener {
                _uploadSuccess.value = true
                Log.d("Firestore", "Reporte guardado correctamente en Firestore con ID: ${reporte.reporteId}")
                onResult(true)
            }
            .addOnFailureListener { exception ->
                _errorMessage.value = "Error al guardar el reporte en Firestore"
                Log.e("Firestore", "Error: ${exception.message}")
                onResult(false)
            }
    }


    fun TakePicture(
        cameraController: LifecycleCameraController,
        executor: Executor,
        context: Context,
        imageFile: File, // Archivo predefinido
        onImageCaptured: (Uri) -> Unit
    ) {
        val outputDirectory = ImageCapture.OutputFileOptions.Builder(imageFile).build()
        cameraController.takePicture(outputDirectory, executor, object : ImageCapture.OnImageSavedCallback {

            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(imageFile)
                onImageCaptured(savedUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Error al guardar la imagen: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }




        fun resetErrorMessage() {
        _errorMessage.value = null
    }

    fun loadBitmapFromUri(context: Context, uri: Uri?): Bitmap? {
        return try {
            uri?.let { BitmapFactory.decodeStream(context.contentResolver.openInputStream(it)) }
        } catch (e: Exception) {
            Log.e("ReportScreen", "Error al cargar la imagen: ${e.localizedMessage}")
            null
        }
    }
}

class SharedViewModel : ViewModel() {
    private val _reportes = mutableStateOf<List<Reporte>>(emptyList())
    val reportes: State<List<Reporte>> = _reportes

    private val db = FirebaseFirestore.getInstance()

    var selectedReporte by mutableStateOf<Reporte?>(null)

    fun updateReport(
        reporteId: String,
        descripcion: String,
        fecha: String,
        imagenUri: String,
        ubicacion: String,
        userId: String,
        onComplete: (Boolean) -> Unit
    ) {
        val updatedReporte = Reporte(
            reporteId = reporteId,
            descripcion = descripcion,
            fecha = fecha,
            imagen = imagenUri,
            ubicacion = ubicacion,
            userId = userId
        )

        db.collection("Reportes")
            .document(updatedReporte.reporteId)
            .set(updatedReporte)
            .addOnSuccessListener {
                onComplete(true) // Éxito en la actualización
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al actualizar el reporte", exception)
                onComplete(false) // Error al actualizar
            }
    }


    // Función para eliminar el reporte
    fun deleteReport(reporteId: String, onComplete: (Boolean) -> Unit) {
        db.collection("Reportes")
            .document(reporteId)
            .delete()
            .addOnSuccessListener {
                onComplete(true) // Éxito en la eliminación
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Error al eliminar el reporte", exception)
                onComplete(false) // Error al eliminar
            }
    }



}



