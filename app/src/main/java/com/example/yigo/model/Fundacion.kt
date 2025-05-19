package com.example.yigo.model

import kotlinx.serialization.Serializable

@Serializable
data class Fundacion(
    val id: String,
    val nombre_fundacion: String,
    val necesidades: String,
    val rut_url: String? = null,
    val camara_url: String? = null,
    val direccion: String,
    val ubicacion_lat: Double,
    val ubicacion_lng: Double,
    val nombre_representante: String,
    val telefono_representante: String
)