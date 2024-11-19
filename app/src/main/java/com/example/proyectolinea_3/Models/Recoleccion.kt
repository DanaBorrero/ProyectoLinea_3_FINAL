package com.example.proyectolinea_3.Models

data class Recoleccion(
    val id: String = "",
    val zona: String = "",
    val tipo_residuo: String = "",
    val fecha_recoleccion: String = "",
    val volumen_residuos: String = ""
) {
    fun toMap(): MutableMap<String, String?> {
        return mutableMapOf(
            "id" to this.id,
            "zona" to this.zona,
            "tipo_residuo" to this.tipo_residuo,
            "fecha_recoleccion" to this.fecha_recoleccion,
            "volumen_residuos" to this.volumen_residuos
        )
    }

    constructor() : this("", "", "", "", "")

}