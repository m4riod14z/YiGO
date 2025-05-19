package com.example.yigo.model

import kotlinx.serialization.Serializable

@Serializable
data class Calificaciones(
    val id: String,
    val id_donacion: String,
    val puntuacion: Int
)