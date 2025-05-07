package com.example.yigo.controller

import android.util.Log
import com.example.yigo.model.SupabaseService
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.HttpRequestException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AuthController {

    fun iniciarSesion(correo: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("Login", "Intentando iniciar sesión con: $correo")
                SupabaseService.auth.signInWith(Email) {
                    this.email = correo.trim()
                    this.password = password
                }

                val user = SupabaseService.auth.currentUserOrNull()
                if (user != null) {
                    Log.d("Login", "Sesión iniciada correctamente. UID: ${user.id}")
                } else {
                    Log.e("Login", "No se encontró el usuario tras iniciar sesión.")
                }

            } catch (e: HttpRequestException) {
                Log.e("Login", "Error HTTP: ${e.message}", e)
            } catch (e: Exception) {
                Log.e("Login", "Error general: ${e.message}", e)
            }
        }
    }

    fun cerrarSesion() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseService.auth.signOut()
                Log.d("Logout", "Sesión cerrada correctamente.")
            } catch (e: HttpRequestException) {
                Log.e("Logout", "Error HTTP al cerrar sesión: ${e.message}", e)
            } catch (e: Exception) {
                Log.e("Logout", "Error general al cerrar sesión: ${e.message}", e)
            }
        }
    }
}