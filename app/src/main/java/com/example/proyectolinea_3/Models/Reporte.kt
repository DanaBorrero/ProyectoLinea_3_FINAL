package com.example.proyectolinea_3.Models

data class Reporte(
    val reporteId: String = "",
    val userId: String = "",
    var descripcion: String = "",
    val fecha: String = "",
    var ubicacion: String = "",
    var imagen: String? = null
) {
    fun toMap(): MutableMap<String, String?> {
        return mutableMapOf(
            "reporteId" to this.reporteId,
            "userId" to this.userId,
            "descripcion" to this.descripcion,
            "fecha" to this.fecha,
            "ubicacion" to this.ubicacion,
            "imagen" to this.imagen  // Añadir el campo imagen
        )
    }

    constructor() : this("", "", "", "", "",null)
}

