package com.example.yigo.controller

import android.util.Log
import com.example.yigo.model.SupabaseService
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import com.example.yigo.model.Usuario
import io.github.jan.supabase.postgrest.query.Columns

object AuthController {

    suspend fun iniciarSesion(correo: String, password: String): Triple<Boolean, String, String?> {
        return try {
            Log.d("Login", "Intentando iniciar sesión con: $correo")
            SupabaseService.auth.signInWith(Email) {
                this.email = correo.trim()
                this.password = password
            }

            val user = SupabaseService.auth.currentUserOrNull()
            if (user != null) {
                Log.d("Login", "Autenticación correcta. UID: ${user.id}")

                // Verificar estado del usuario en la base de datos
                try {
                    val response = SupabaseService.database
                        .from("usuarios")
                        .select {
                            filter {
                                eq("id", user.id)
                            }
                            limit(1)
                        }
                        .decodeSingle<Usuario>()

                    if (response.estado == "activo") {
                        Log.d("Login", "Usuario activo, acceso concedido. Tipo: ${response.tipo}")
                        Triple(true, "Inicio de sesión exitoso", response.tipo)
                    } else {
                        // Si el usuario no está activo, cerrar sesión inmediatamente
                        SupabaseService.auth.signOut()
                        Log.d("Login", "Usuario inactivo, acceso denegado")
                        Triple(false, "Tu cuenta está desactivada. Contacta con soporte.", null)
                    }
                } catch (e: Exception) {
                    // Si hay un error al obtener el estado, cerrar sesión por seguridad
                    SupabaseService.auth.signOut()
                    Log.e("Login", "Error al verificar estado: ${e.message}", e)
                    Triple(false, "Error al verificar estado de la cuenta", null)
                }
            } else {
                Log.e("Login", "No se encontró el usuario tras iniciar sesión")
                Triple(false, "Error al iniciar sesión", null)
            }
        } catch (e: HttpRequestException) {
            Log.e("Login", "Error HTTP: ${e.message}", e)
            Triple(false, "Credenciales incorrectas o problemas de conexión", null)
        } catch (e: Exception) {
            Log.e("Login", "Error general: ${e.message}", e)
            Triple(false, "Error al iniciar sesión: ${e.message}", null)
        }
    }

    suspend fun cerrarSesion(): Boolean {
        return try {
            SupabaseService.auth.signOut()
            Log.d("Logout", "Sesión cerrada correctamente")
            true
        } catch (e: HttpRequestException) {
            Log.e("Logout", "Error HTTP al cerrar sesión: ${e.message}", e)
            false
        } catch (e: Exception) {
            Log.e("Logout", "Error general al cerrar sesión: ${e.message}", e)
            false
        }
    }

    suspend fun obtenerTipoUsuario(userId: String): String? {
        return try {
            val response = SupabaseService.database
                .from("usuarios")
                .select(columns = Columns.list("id, correo, telefono, tipo, estado"))
                {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Usuario>()

            response.tipo
        } catch (e: Exception) {
            Log.e("AuthController", "Error al obtener tipo de usuario: ${e.message}")
            null
        }
    }
}