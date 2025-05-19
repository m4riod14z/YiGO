package com.example.yigo.controller

import io.github.jan.supabase.postgrest.query.Columns
import android.util.Log
import com.example.yigo.model.SupabaseService
import com.example.yigo.model.Usuario
import io.github.jan.supabase.exceptions.HttpRequestException

object AdminUsersController {

    // Alternar entre estado activo e inactivo
    suspend fun alternarEstadoUsuario(idUsuario: String): Boolean {
        return try {
            val usuario = SupabaseService.database
                .from("usuarios")
                .select {
                    filter { eq("id", idUsuario) }
                    limit(1)
                }
                .decodeSingle<Usuario>()

            val nuevoEstado = if (usuario.estado == "activo") "inactivo" else "activo"

            SupabaseService.database.from("usuarios").update(
                mapOf("estado" to nuevoEstado)
            ) {
                filter { eq("id", idUsuario) }
            }

            Log.d("Admin", "Estado del usuario $idUsuario actualizado a $nuevoEstado")
            true
        } catch (e: HttpRequestException) {
            Log.e("Admin", "Error HTTP al alternar estado: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e("Admin", "Error general al alternar estado: ${e.message}", e)
            false
        }
    }

    // Obtener Donantes (Persona y Empresa)
    suspend fun obtenerDonantes(): List<Map<String, Any?>> {
        return try {
            val usuarios = SupabaseService.database
                .from("usuarios")
                .select()
                .decodeList<Usuario>()

            val personas = SupabaseService.database
                .from("personas")
                .select(columns = Columns.list("id, nombre, apellido"))
                .decodeList<Map<String, String>>()

            val empresas = SupabaseService.database
                .from("empresas")
                .select(columns = Columns.list("id, razon_social, nit, rut_url"))
                .decodeList<Map<String, String>>()

            val donantes = usuarios.filter { it.tipo == "persona" || it.tipo == "empresa" }

            donantes.map { usuario ->
                val nombre = when (usuario.tipo) {
                    "persona" -> personas.find { it["id"] == usuario.id }?.get("nombre") ?: "No disponible"
                    "empresa" -> empresas.find { it["id"] == usuario.id }?.get("razon_social") ?: "No disponible"
                    else -> "No disponible"
                }

                mapOf(
                    "usuario" to usuario,
                    "nombre" to nombre
                )
            }
        } catch (e: Exception) {
            Log.e("Admin", "Error al obtener donantes: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerFundaciones(): List<Map<String, Any?>> {
        return try {
            val usuarios = SupabaseService.database
                .from("usuarios")
                .select()
                .decodeList<Usuario>()

            val fundaciones = SupabaseService.database
                .from("fundaciones")
                .select(columns = Columns.list("id, nombre_fundacion, necesidades, direccion, nombre_representante, telefono_representante"))
                .decodeList<Map<String, String>>()

            val usuariosFundacion = usuarios.filter { it.tipo == "fundacion" }

            usuariosFundacion.map { usuario ->
                val fundacionData = fundaciones.find { it["id"] == usuario.id }
                if (fundacionData != null) {
                    mapOf(
                        "usuario" to usuario,
                        "fundacion" to fundacionData
                    )
                } else {
                    Log.w("Admin", "Fundación no encontrada para el usuario con ID: ${usuario.id}")
                    null
                }
            }.filterNotNull()

        } catch (e: Exception) {
            Log.e("Admin", "Error al obtener fundaciones: ${e.message}")
            emptyList()
        }
    }
}