package com.example.proyectolinea_3.Models

data class User(
    val userId: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val password: String,
    val celular: String,
    val ubicacion: String
){
    fun toMap(): MutableMap<String, Any> {
        return mutableMapOf(
            "userId" to this.userId,
            "nombre" to this.nombre,
            "apellido" to this.apellido,
            "email" to this.email,
            "password" to this.password,
            "celular" to this.celular,
            "ubicacion" to this.ubicacion
        )
    }

}
