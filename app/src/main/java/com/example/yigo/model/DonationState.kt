package com.example.yigo.model

sealed class DonationState(val value: String) {
    object Publicada : DonationState("publicada")
    object Asignada : DonationState("asignada")
    object Aceptada : DonationState("aceptada")
    object Entregada : DonationState("entregada")

    companion object {
        fun from(value: String): DonationState = when (value) {
            "publicada" -> Publicada
            "asignada" -> Asignada
            "aceptada" -> Aceptada
            "entregada" -> Entregada
            else -> throw IllegalArgumentException("Estado no válido")
        }
    }
}