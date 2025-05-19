package com.example.yigo.controller

import android.util.Log
import com.example.yigo.model.Empresa
import com.example.yigo.model.Fundacion
import com.example.yigo.model.Persona
import com.example.yigo.model.SupabaseService
import com.example.yigo.model.Usuario
import io.github.jan.supabase.postgrest.query.Columns

object ProfileController {

    suspend fun obtenerDatosPersona(userId: String): Pair<Usuario?, Persona?> {
        return try {
            // Consulta 1: Datos del usuario (correo y teléfono)
            val usuario = SupabaseService.database
                .from("usuarios")
                .select(columns = Columns.list("id, correo, telefono, tipo, estado")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Usuario>()

            // Consulta 2: Datos de la persona (nombre y apellido)
            val persona = SupabaseService.database
                .from("personas")
                .select(columns = Columns.list("id, nombre, apellido")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Persona>()

            Pair(usuario, persona)

        } catch (e: Exception) {
            Log.e("ProfileController", "Error al obtener datos del usuario: ${e.message}")
            Pair(null, null)
        }
    }

    suspend fun obtenerDatosEmpresa(userId: String): Pair<Usuario?, Empresa?> {
        return try {
            // Consulta 1: Datos del usuario (correo y teléfono)
            val usuario = SupabaseService.database
                .from("usuarios")
                .select(columns = Columns.list("id, correo, telefono, tipo, estado")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Usuario>()

            // Consulta 2: Datos de la empresa (razon_social)
            val empresa = SupabaseService.database
                .from("empresas")
                .select(columns = Columns.list("id, razon_social, nit, rut_url")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Empresa>()

            Pair(usuario, empresa)

        } catch (e: Exception) {
            Log.e("ProfileController", "Error al obtener datos de empresa: ${e.message}")
            Pair(null, null)
        }
    }

    suspend fun obtenerDatosFundacion(userId: String): Pair<Usuario?, Fundacion?> {
        return try {
            // Consulta 1: Datos del usuario (correo y teléfono)
            val usuario = SupabaseService.database
                .from("usuarios")
                .select(columns = Columns.list("id, correo, telefono, tipo, estado")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Usuario>()

            // Consulta 2: Datos de la fundación
            val fundacion = SupabaseService.database
                .from("fundaciones")
                .select(columns = Columns.list("id, nombre_fundacion, necesidades, rut_url, camara_url, direccion, ubicacion_lat, ubicacion_lng, nombre_representante, telefono_representante")) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingleOrNull<Fundacion>()

            Pair(usuario, fundacion)

        } catch (e: Exception) {
            Log.e("ProfileController", "Error al obtener datos de fundación: ${e.message}")
            Pair(null, null)
        }
    }

    suspend fun editarPersona(id: String, nombre: String, apellido: String, telefono: String): Boolean {
        return try {
            // Actualizar en personas
            SupabaseService.database.from("personas")
                .update(
                    mapOf(
                        "nombre" to nombre.trim(),
                        "apellido" to apellido.trim()
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }

            // Actualizar teléfono en usuarios
            actualizarTelefonoUsuario(id, telefono)

            Log.d("ProfileController", "Información de persona actualizada correctamente")
            true
        } catch (e: Exception) {
            Log.e("ProfileController", "Error al editar persona: ${e.message}", e)
            false
        }
    }

    suspend fun editarEmpresa(id: String, razonSocial: String, nit: String, telefono: String): Boolean {
        return try {
            // Actualizar en empresas
            SupabaseService.database.from("empresas")
                .update(
                    mapOf(
                        "razon_social" to razonSocial.trim(),
                        "nit" to nit.trim()
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }

            // Actualizar teléfono en usuarios
            actualizarTelefonoUsuario(id, telefono)

            Log.d("ProfileController", "Información de empresa actualizada correctamente")
            true
        } catch (e: Exception) {
            Log.e("ProfileController", "Error al editar empresa: ${e.message}", e)
            false
        }
    }

    suspend fun editarFundacion(
        id: String,
        nombre: String,
        necesidades: String,
        direccion: String,
        nombreRepresentante: String,
        telefonoRepresentante: String
    ): Boolean {
        return try {
            // Actualizar en fundaciones
            SupabaseService.database.from("fundaciones")
                .update(
                    mapOf(
                        "nombre_fundacion" to nombre.trim(),
                        "necesidades" to necesidades.trim(),
                        "direccion" to direccion.trim(),
                        "nombre_representante" to nombreRepresentante.trim(),
                        "telefono_representante" to telefonoRepresentante.trim()
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }

            Log.d("ProfileController", "Información de fundación actualizada correctamente")
            true
        } catch (e: Exception) {
            Log.e("ProfileController", "Error al editar fundación: ${e.message}", e)
            false
        }
    }

    private suspend fun actualizarTelefonoUsuario(id: String, telefono: String): Boolean {
        return try {
            SupabaseService.database.from("usuarios")
                .update(mapOf("telefono" to telefono.trim()))
                {
                    filter {
                        eq("id", id)
                    }
                }

            Log.d("ProfileController", "Teléfono actualizado en usuarios")
            true
        } catch (e: Exception) {
            Log.e("ProfileController", "Error al actualizar teléfono en usuarios: ${e.message}", e)
            false
        }
    }
}