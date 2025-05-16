package com.example.yigo.controller

import android.util.Log
import com.example.yigo.model.Calificaciones
import com.example.yigo.model.Donaciones
import com.example.yigo.model.DonationState
import com.example.yigo.model.SupabaseService
import com.example.yigo.model.Usuario
import com.example.yigo.model.Persona
import com.example.yigo.model.Empresa
import com.example.yigo.model.Fundacion
import com.example.yigo.model.TipoAlimento
import io.github.jan.supabase.postgrest.query.Columns
import java.util.Objects.isNull

object DonationProcessController {

    suspend fun obtenerDonacionesPendientes(
        fundacionId: String
    ): List<Map<String, Any?>> {
        return try {
            val tiposAlimento = obtenerTiposAlimento().associateBy { it.id }
            val donaciones = SupabaseService.database
                .from("donaciones")
                .select(
                    columns = Columns.list(
                        "id, id_donante, imagen_url, estado, id_fundacion, ubicacion_lat, ubicacion_lng, direccion, productos, tipo_alimento_id, descripcion"
                    )
                ) {
                    filter {
                        or {
                            and {
                                eq("estado", DonationState.Publicada.value)
                                isNull("id_fundacion")
                            }
                            and {
                                eq("estado", DonationState.Asignada.value)
                                eq("id_fundacion", fundacionId)
                            }
                        }
                    }
                }
                .decodeList<Donaciones>()

            donaciones.map { donacion ->
                val usuario = obtenerUsuario(donacion.id_donante)
                val nombreDonante = when (usuario.tipo) {
                    "persona" -> obtenerNombrePersona(usuario.id)
                    "empresa" -> obtenerRazonSocialEmpresa(usuario.id)
                    else -> "No disponible"
                }

                val nombreFundacion = donacion.id_fundacion?.let { obtenerNombreFundacion(it) } ?: "No asignada"
                val tipoAlimento = tiposAlimento[donacion.tipo_alimento_id]?.tipo ?: "No disponible"

                mapOf(
                    "donacion" to donacion,
                    "nombre_donante" to nombreDonante,
                    "telefono_donante" to usuario.telefono,
                    "donante_tipo" to usuario.tipo,
                    "tipo_alimento" to tipoAlimento,
                    "nombre_fundacion" to nombreFundacion
                )
            }

        } catch (e: Exception) {
            Log.e("DonationProcess", "Error al obtener donaciones: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerDonacionesCompletadas(fundacionId: String): List<Map<String, Any?>> {
        return try {
            val tiposAlimento = obtenerTiposAlimento().associateBy { it.id }
            val donaciones = SupabaseService.database
                .from("donaciones")
                .select(
                    columns = Columns.list(
                        "id, id_donante, imagen_url, estado, id_fundacion, ubicacion_lat, ubicacion_lng, direccion, productos, tipo_alimento_id, descripcion"
                    )
                ) {
                    filter {
                        or {
                            and {
                                eq("estado", DonationState.Aceptada.value)
                                eq("id_fundacion", fundacionId)
                            }
                            and {
                                eq("estado", DonationState.Entregada.value)
                                eq("id_fundacion", fundacionId)
                            }
                        }
                    }
                }
                .decodeList<Donaciones>()

            donaciones.map { donacion ->
                val usuario = obtenerUsuario(donacion.id_donante)
                val nombreDonante = when (usuario.tipo) {
                    "persona" -> obtenerNombrePersona(usuario.id)
                    "empresa" -> obtenerRazonSocialEmpresa(usuario.id)
                    else -> "No disponible"
                }

                val nombreFundacion = donacion.id_fundacion?.let { obtenerNombreFundacion(it) } ?: "No asignada"
                val tipoAlimento = tiposAlimento[donacion.tipo_alimento_id]?.tipo ?: "No disponible"
                val calificacion = obtenerCalificacion(donacion.id)

                mapOf(
                    "donacion" to donacion,
                    "nombre_donante" to nombreDonante,
                    "telefono_donante" to usuario.telefono,
                    "donante_tipo" to usuario.tipo,
                    "tipo_alimento" to tipoAlimento,
                    "nombre_fundacion" to nombreFundacion,
                    "puntuacion" to calificacion
                )
            }

        } catch (e: Exception) {
            Log.e("DonationProcess", "Error al obtener donaciones completadas: ${e.message}")
            emptyList()
        }
    }

    private suspend fun obtenerNombreFundacion(fundacionId: String): String {
        return try {
            val fundacion = SupabaseService.database
                .from("fundaciones")
                .select(
                    columns = Columns.list("id, nombre_fundacion, necesidades, rut_url, camara_url, direccion, ubicacion_lat, ubicacion_lng, nombre_representante, telefono_representante")
                ) {
                    filter {
                        eq("id", fundacionId)
                    }
                }
                .decodeSingle<Fundacion>()

            fundacion.nombre_fundacion

        } catch (e: Exception) {
            Log.e("DonationController", "Error al obtener nombre de fundación: ${e.message}")
            "No asignada"
        }
    }

    suspend fun aceptarDonacion(donacionId: String, fundacionId: String): Boolean {
        return try {
            SupabaseService.database
                .from("donaciones")
                .update(
                    mapOf(
                        "estado" to DonationState.Aceptada.value,
                        "id_fundacion" to fundacionId
                    )
                ) {
                    filter {
                        eq("id", donacionId)
                    }
                }

            true
        } catch (e: Exception) {
            Log.e("DonationController", "Error al aceptar donación: ${e.message}")
            false
        }
    }

    suspend fun rechazarDonacion(donacionId: String): Boolean {
        return try {
            SupabaseService.database
                .from("donaciones")
                .update(
                    mapOf(
                        "estado" to DonationState.Publicada.value,
                        "id_fundacion" to null // Eliminamos la fundación asignada
                    )
                ) {
                    filter {
                        eq("id", donacionId)
                    }
                }
            true
        } catch (e: Exception) {
            Log.e("DonationController", "Error al rechazar donación: ${e.message}")
            false
        }
    }

    suspend fun marcarComoEntregada(donacionId: String): Boolean {
        return actualizarEstadoDonacion(donacionId, DonationState.Entregada)
    }

    // Actualizamos la función actualizarEstadoDonacion para aceptar DonationState
    private suspend fun actualizarEstadoDonacion(donacionId: String, estado: DonationState): Boolean {
        return try {
            SupabaseService.database
                .from("donaciones")
                .update(mapOf("estado" to estado.value))
                {
                    filter{
                        eq("id", donacionId)
                    }
                }
            true
        } catch (e: Exception) {
            Log.e("DonationController", "Error al actualizar estado de donación: ${e.message}")
            false
        }
    }

    suspend fun calificarDonacion(donacionId: String, puntuacion: Int): Boolean {
        return try {
            // Verificar si ya existe una calificación
            val calificacionExistente = SupabaseService.database
                .from("calificaciones")
                .select(columns = Columns.list("id, id_donacion, puntuacion"))
                {
                    filter {
                        eq("id_donacion", donacionId)
                    }
                }
                .decodeList<Calificaciones>()

            if (calificacionExistente.isNotEmpty()) {
                // Actualizar calificación existente
                val calificacionId = calificacionExistente.first().id

                SupabaseService.database
                    .from("calificaciones")
                    .update(
                        Calificaciones(
                            id = calificacionId,
                            id_donacion = donacionId,
                            puntuacion = puntuacion
                        )
                    ) {
                        filter { eq("id", calificacionId) }
                    }

            } else {
                // Insertar nueva calificación
                val idCalificacion = java.util.UUID.randomUUID().toString()

                SupabaseService.database
                    .from("calificaciones")
                    .insert(
                        Calificaciones(
                            id = idCalificacion,
                            id_donacion = donacionId,
                            puntuacion = puntuacion
                        )
                    )
            }

            true
        } catch (e: Exception) {
            Log.e("DonationProcess", "Error al calificar donación: ${e.message}")
            false
        }
    }

    suspend fun obtenerCalificacion(donacionId: String): Calificaciones? {
        return try {
            SupabaseService.database
                .from("calificaciones")
                .select()
                {
                    filter {
                        eq("id_donacion", donacionId)
                    }
                }
                .decodeSingleOrNull()
        } catch (e: Exception) {
            Log.e("DonationProcess", "Error al obtener calificación: ${e.message}")
            null
        }
    }

    private suspend fun obtenerTiposAlimento(): List<TipoAlimento> {
        return try {
            SupabaseService.database.from("tipoalimento")
                .select()
                .decodeList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private suspend fun obtenerUsuario(userId: String): Usuario {
        return try {
            SupabaseService.database.from("usuarios")
                .select(columns = Columns.list("id, correo, tipo, telefono, estado"))
                {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle()
        } catch (e: Exception) {
            Usuario("", "", "", "")
        }
    }

    private suspend fun obtenerNombrePersona(userId: String): String {
        return try {
            SupabaseService.database.from("personas")
                .select(columns = Columns.list("id, nombre, apellido"))
                {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Persona>().nombre
        } catch (e: Exception) {
            "No disponible"
        }
    }

    private suspend fun obtenerRazonSocialEmpresa(userId: String): String {
        return try {
            SupabaseService.database.from("empresas")
                .select(columns = Columns.list("id, razon_social, nit, rut_url"))
                {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Empresa>().razon_social
        } catch (e: Exception) {
            "No disponible"
        }
    }
}