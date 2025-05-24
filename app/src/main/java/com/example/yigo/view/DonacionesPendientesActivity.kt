package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R
import com.example.yigo.controller.DonationProcessController
import com.example.yigo.model.DonacionesPendientesAdapter
import com.example.yigo.model.SupabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DonacionesPendientesActivity : AppCompatActivity() {

    private lateinit var rvDonacionesPendientes: RecyclerView
    private lateinit var tvNoDonaciones: TextView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donaciones_pendientes_persona)

        rvDonacionesPendientes = findViewById(R.id.recyclerViewDonacionesPendientesPersona)
        tvNoDonaciones = findViewById(R.id.tvNoDonacionesPendientes)

        rvDonacionesPendientes.layoutManager = LinearLayoutManager(this)

        userId = SupabaseService.auth.currentUserOrNull()?.id

        userId?.let {
            cargarDonacionesPendientes(it)
        }
    }

    private fun cargarDonacionesPendientes(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val donacionesPendientes = DonationProcessController.obtenerDonacionesPendientes(userId)


                withContext(Dispatchers.Main) {
                    if (donacionesPendientes.isEmpty()) {
                        tvNoDonaciones.visibility = View.VISIBLE
                        rvDonacionesPendientes.visibility = View.GONE
                    } else {
                        tvNoDonaciones.visibility = View.GONE
                        rvDonacionesPendientes.visibility = View.VISIBLE

                        val adapter = DonacionesPendientesAdapter(
                            donacionesPendientes,
                            onAceptarClick = { donacion ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val userId = SupabaseService.auth.currentUserOrNull()?.id

                                    if (userId != null) {
                                        val resultado = DonationProcessController.aceptarDonacion(donacion.id, userId)
                                        withContext(Dispatchers.Main) {
                                            if (resultado) {
                                                Toast.makeText(this@DonacionesPendientesActivity, "Donación aceptada", Toast.LENGTH_SHORT).show()
                                                cargarDonacionesPendientes(userId)
                                            } else {
                                                Toast.makeText(this@DonacionesPendientesActivity, "Error al aceptar donación", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(this@DonacionesPendientesActivity, "Error al obtener ID de fundación", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onRechazarClick = { donacion ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val resultado = DonationProcessController.rechazarDonacion(donacion.id)
                                    withContext(Dispatchers.Main) {
                                        if (resultado) {
                                            Toast.makeText(this@DonacionesPendientesActivity, "Donación rechazada", Toast.LENGTH_SHORT).show()
                                            cargarDonacionesPendientes(userId)
                                        } else {
                                            Toast.makeText(this@DonacionesPendientesActivity, "Error al rechazar donación", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onVerUbicacionClick = { donacion ->
                                val intent = Intent(this@DonacionesPendientesActivity, MapActivity::class.java).apply {
                                    putExtra("ubicacion_lat", donacion.ubicacion_lat ?: 0.0)
                                    putExtra("ubicacion_lng", donacion.ubicacion_lng ?: 0.0)
                                }
                                startActivity(intent)
                            }

                        )

                        rvDonacionesPendientes.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("DonacionesPendientes", "Error al cargar donaciones: ${e.message}")
                    Toast.makeText(this@DonacionesPendientesActivity, "Error al cargar donaciones", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}