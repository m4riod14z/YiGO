package com.example.yigo.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R
import com.example.yigo.controller.DonationController
import com.example.yigo.model.DonacionUsuarioAdapter
import com.example.yigo.model.SupabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerDonacionesUsuario : AppCompatActivity() {

    private lateinit var rvDonacionesUsuario: RecyclerView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_donaciones_usuario)

        rvDonacionesUsuario = findViewById(R.id.recyclerViewDonacionesUsuario)
        rvDonacionesUsuario.layoutManager = LinearLayoutManager(this)

        userId = SupabaseService.auth.currentUserOrNull()?.id

        if (userId != null) {
            cargarDonacionesUsuario(userId!!)
        } else {
            Toast.makeText(this, "Error al obtener el usuario actual", Toast.LENGTH_SHORT).show()
            Log.e("VerDonacionesUsuario", "userId es null")
        }
    }

    private fun cargarDonacionesUsuario(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val donaciones = DonationController.obtenerDonacionesPorUsuario(userId)

                withContext(Dispatchers.Main) {
                    val noDonacionesTextView = findViewById<TextView>(R.id.tvNoDonaciones)

                    if (donaciones.isEmpty()) {
                        noDonacionesTextView.visibility = View.VISIBLE
                        rvDonacionesUsuario.visibility = View.GONE
                    } else {
                        noDonacionesTextView.visibility = View.GONE
                        rvDonacionesUsuario.visibility = View.VISIBLE

                        val adapter = DonacionUsuarioAdapter(
                            donaciones,
                            onDeleteClick = { donacion ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val resultado = DonationController.eliminarDonacion(donacion.id, donacion.imagen_url)
                                    withContext(Dispatchers.Main) {
                                        if (resultado) {
                                            Toast.makeText(this@VerDonacionesUsuario, "Donación eliminada", Toast.LENGTH_SHORT).show()
                                            cargarDonacionesUsuario(userId)
                                        } else {
                                            Toast.makeText(this@VerDonacionesUsuario, "Error al eliminar donación", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )

                        rvDonacionesUsuario.adapter = adapter
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@VerDonacionesUsuario, "Error al cargar donaciones", Toast.LENGTH_SHORT).show()
                    Log.e("VerDonacionesUsuario", "Error: ${e.message}")
                }
            }
        }
    }
}