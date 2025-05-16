package com.example.yigo.controller

import android.util.Log
import com.example.yigo.model.Fundacion
import com.example.yigo.model.SupabaseService
import io.github.jan.supabase.postgrest.query.Columns

object FoundationController {

    suspend fun obtenerFundaciones(): List<Fundacion> {
        return try {
            val columns = Columns.raw(
                """
            id,
            nombre_fundacion,
            necesidades,
            direccion,
            nombre_representante,
            telefono_representante,
            ubicacion_lat,
            ubicacion_lng
            """.trimIndent()
            )

            SupabaseService.database.from("fundaciones")
                .select(columns = columns)
                .decodeList<Fundacion>()

        } catch (e: Exception) {
            Log.e("FoundationController", "Error al obtener fundaciones: ${e.message}", e)
            emptyList()
        }
    }
}