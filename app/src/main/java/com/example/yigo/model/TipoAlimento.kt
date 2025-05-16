package com.example.yigo.model

import kotlinx.serialization.Serializable

@Serializable
data class TipoAlimento(
    val id: Int,
    val tipo: String
)