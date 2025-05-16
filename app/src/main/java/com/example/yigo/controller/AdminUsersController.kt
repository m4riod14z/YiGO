package com.example.yigo.controller

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
}