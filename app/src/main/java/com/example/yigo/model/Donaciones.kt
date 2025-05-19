package com.example.yigo.model

import kotlinx.serialization.Serializable

@Serializable
data class Donaciones(
    val id: String,
    val id_donante: String,
    val imagen_url: String,
    val estado: String = DonationState.Publicada.value,
    val id_fundacion: String? = null,
    val ubicacion_lat: Double?,
    val ubicacion_lng: Double?,
    val direccion: String,
    val productos: String,
    val tipo_alimento_id: Int,
    val descripcion: String
)