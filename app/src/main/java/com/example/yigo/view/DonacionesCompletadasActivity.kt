package com.example.yigo.view

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R
import com.example.yigo.controller.DonationProcessController
import com.example.yigo.model.DonacionesCompletadasAdapter
import com.example.yigo.model.SupabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DonacionesCompletadasActivity : AppCompatActivity() {

    private lateinit var rvDonacionesCompletadas: RecyclerView
    private lateinit var tvNoDonaciones: TextView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donaciones_completadas_personas)

        rvDonacionesCompletadas = findViewById(R.id.recyclerViewDonacionesCompletadasPersona)
        tvNoDonaciones = findViewById(R.id.tvNoDonacionesCompletadas)

        rvDonacionesCompletadas.layoutManager = LinearLayoutManager(this)

        userId = SupabaseService.auth.currentUserOrNull()?.id

        userId?.let {
            cargarDonacionesCompletadas(it)
        }
    }

    private fun cargarDonacionesCompletadas(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val donacionesCompletadas = DonationProcessController.obtenerDonacionesCompletadas(
                    fundacionId = userId
                )

                withContext(Dispatchers.Main) {
                    if (donacionesCompletadas.isEmpty()) {
                        tvNoDonaciones.visibility = View.VISIBLE
                        rvDonacionesCompletadas.visibility = View.GONE
                    } else {
                        tvNoDonaciones.visibility = View.GONE
                        rvDonacionesCompletadas.visibility = View.VISIBLE

                        val adapter = DonacionesCompletadasAdapter(
                            donacionesCompletadas,
                            onEntregadaClick = { donacion ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val resultado = DonationProcessController.marcarComoEntregada(donacion.id)
                                    withContext(Dispatchers.Main) {
                                        if (resultado) {
                                            Toast.makeText(this@DonacionesCompletadasActivity, "Donación marcada como entregada", Toast.LENGTH_SHORT).show()
                                            cargarDonacionesCompletadas(userId)
                                        }
                                        else {
                                            Toast.makeText(this@DonacionesCompletadasActivity, "Error al marcar la donación como entregada", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            },
                            onCalificarClick = { donacion ->
                                mostrarDialogoCalificacion(donacion.id)
                            },
                            onVerUbicacionClick = { donacion ->
                                val intent = Intent(this@DonacionesCompletadasActivity, MapActivity::class.java)
                                intent.putExtra("ubicacion_lat", donacion.ubicacion_lat)
                                intent.putExtra("ubicacion_lng", donacion.ubicacion_lng)
                                startActivity(intent)
                            }
                        )

                        rvDonacionesCompletadas.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("DonacionesCompletadas", "Error al cargar donaciones: ${e.message}")
                }
            }
        }
    }

    private fun mostrarDialogoCalificacion(donacionId: String) {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_calificar, null)
        dialog.setContentView(view)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBarCalificacion)
        val btnEnviar = view.findViewById<Button>(R.id.btnEnviarCalificacion)

        btnEnviar.setOnClickListener {
            val puntuacion = ratingBar.rating.toInt()

            if (puntuacion == 0) {
                Toast.makeText(this, "Selecciona una calificación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val resultado = DonationProcessController.calificarDonacion(donacionId, puntuacion)
                withContext(Dispatchers.Main) {
                    if (resultado) {
                        Toast.makeText(this@DonacionesCompletadasActivity, "Calificación enviada", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        // Actualizar la lista después de calificar
                        userId?.let { cargarDonacionesCompletadas(it) }
                    } else {
                        Toast.makeText(this@DonacionesCompletadasActivity, "Error al enviar calificación", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        dialog.show()
    }
}