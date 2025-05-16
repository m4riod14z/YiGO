package com.example.yigo.controller

import android.util.Log
import com.example.yigo.model.Donaciones
import com.example.yigo.model.TipoAlimento
import com.example.yigo.model.SupabaseService
import com.example.yigo.model.Usuario
import com.example.yigo.model.Persona
import com.example.yigo.model.Empresa
import com.example.yigo.model.Fundacion
import io.github.jan.supabase.postgrest.query.Columns
import java.util.UUID

object DonationController {

    suspend fun obtenerDonacionesPorUsuario(userId: String): List<Map<String, Any?>> {
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
                        eq("id_donante", userId)
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
                    "nombre_fundacion" to nombreFundacion,
                    "tipo_alimento" to tipoAlimento
                )
            }

        } catch (e: Exception) {
            Log.e("DonationController", "Error al obtener donaciones: ${e.message}")
            emptyList()
        }
    }

    private suspend fun obtenerUsuario(userId: String): Usuario {
        return try {
            SupabaseService.database
                .from("usuarios")
                .select(
                    columns = Columns.list("id, correo, telefono, tipo, estado")
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle()
        } catch (e: Exception) {
            Log.e("DonationController", "Error al obtener usuario: ${e.message}")
            Usuario("", "", "", "")
        }
    }

    private suspend fun obtenerNombrePersona(userId: String): String {
        return try {
            val persona = SupabaseService.database
                .from("personas")
                .select(
                    columns = Columns.list("id, nombre, apellido")
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Persona>()

            persona.nombre

        } catch (e: Exception) {
            Log.e("DonationController", "Error al obtener nombre de persona: ${e.message}")
            "No disponible"
        }
    }

    private suspend fun obtenerRazonSocialEmpresa(userId: String): String {
        return try {
            val empresa = SupabaseService.database
                .from("empresas")
                .select(
                    columns = Columns.list("id, razon_social, nit, rut_url")
                ) {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<Empresa>()

            empresa.razon_social

        } catch (e: Exception) {
            Log.e("DonationController", "Error al obtener razón social de empresa: ${e.message}")
            "No disponible"
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

    suspend fun subirImagenDonacion(fileName: String, fileBytes: ByteArray): String? {
        return try {
            SupabaseService.storage
                .from("donation-bucket")
                .upload(
                    path = fileName,
                    data = fileBytes
                ) {
                    upsert = true
                }

            val publicUrl = SupabaseService.storage
                .from("donation-bucket")
                .publicUrl(fileName)

            Log.d("DonationController", "Imagen subida: $publicUrl")
            publicUrl
        } catch (e: Exception) {
            Log.e("DonationController", "Error al subir imagen: ${e.message}", e)
            null
        }
    }

    // Crear una donación con dirección e imagen
    suspend fun crearDonacionConDatos(
        direccion: String,
        descripcion: String,
        tipoAlimentoId: Int,
        imagenUrl: String,
        ubicacionLat: Double? = 0.0,
        ubicacionLng: Double? = 0.0,
        productos: String,
        fundacionId: String? = null
    ): String? {
        return try {
            val idDonante = SupabaseService.auth.currentUserOrNull()?.id ?: return null

            val donacionID = UUID.randomUUID().toString()

            val nuevaDonacion = Donaciones(
                id = donacionID,
                id_donante = idDonante,
                direccion = direccion,
                descripcion = descripcion,
                tipo_alimento_id = tipoAlimentoId,
                imagen_url = imagenUrl,
                ubicacion_lat = ubicacionLat,
                ubicacion_lng = ubicacionLng,
                productos = productos,
                estado = if (fundacionId != null) "asignada" else "publicada",
                id_fundacion = fundacionId
            )

            val response = SupabaseService.database.from("donaciones")
                .insert(nuevaDonacion) { select() }
                .decodeSingle<Donaciones>()

            Log.d("Donacion", "Donación creada con ID: ${response.id}")
            response.id
        } catch (e: Exception) {
            Log.e("Donacion", "Error al crear donación: ${e.message}", e)
            null
        }
    }

    suspend fun obtenerTiposAlimento(): List<TipoAlimento> {
        return try {
            SupabaseService.database.from("tipoalimento")
                .select()
                .decodeList<TipoAlimento>()
        } catch (e: Exception) {
            Log.e("DonationController", "Error al obtener tipos de alimento: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun eliminarDonacion(donacionId: String, imagenUrl: String?): Boolean {
        return try {
            imagenUrl?.let {
                val basePath = "/storage/v1/object/public/"
                val path = it.substringAfter(basePath)

                if (path.isNotEmpty()) {
                    SupabaseService.storage.from("donation-bucket").delete(path)
                    Log.d("DonationController", "Imagen eliminada: $path")
                } else {
                    Log.w("DonationController", "Path vacío o inválido: $it")
                }
            }

            // Luego, eliminar la donación de la base de datos
            SupabaseService.database
                .from("donaciones")
                .delete {
                    filter {
                        eq("id", donacionId)
                    }
                }

            true
        } catch (e: Exception) {
            Log.e("DonationController", "Error al eliminar donación: ${e.message}", e)
            false
        }
    }
}