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
import com.example.yigo.model.DonacionEmpresaAdapter
import com.example.yigo.model.SupabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerDonacionesEmpresa : AppCompatActivity() {

    private lateinit var rvDonacionesEmpresa: RecyclerView
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_donaciones_empresa)

        rvDonacionesEmpresa = findViewById(R.id.recyclerViewDonacionesEmpresa)
        rvDonacionesEmpresa.layoutManager = LinearLayoutManager(this)

        userId = SupabaseService.auth.currentUserOrNull()?.id

        if (userId != null) {
            cargarDonacionesEmpresa(userId!!)
        } else {
            Toast.makeText(this, "Error al obtener el usuario actual", Toast.LENGTH_SHORT).show()
            Log.e("VerDonacionesEmpresa", "userId es null")
        }
    }

    private fun cargarDonacionesEmpresa(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val donaciones = DonationController.obtenerDonacionesPorUsuario(userId)

                withContext(Dispatchers.Main) {
                    val noDonacionesTextView = findViewById<TextView>(R.id.tvNoDonacionesEmpresa)

                    if (donaciones.isEmpty()) {
                        noDonacionesTextView.visibility = View.VISIBLE
                        rvDonacionesEmpresa.visibility = View.GONE
                    } else {
                        noDonacionesTextView.visibility = View.GONE
                        rvDonacionesEmpresa.visibility = View.VISIBLE

                        val adapter = DonacionEmpresaAdapter(
                            donaciones,
                            onDeleteClick = { donacion ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val resultado = DonationController.eliminarDonacion(donacion.id, donacion.imagen_url)
                                    withContext(Dispatchers.Main) {
                                        if (resultado) {
                                            Toast.makeText(this@VerDonacionesEmpresa, "Donación eliminada", Toast.LENGTH_SHORT).show()
                                            cargarDonacionesEmpresa(userId)
                                        } else {
                                            Toast.makeText(this@VerDonacionesEmpresa, "Error al eliminar donación", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        )

                        rvDonacionesEmpresa.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@VerDonacionesEmpresa, "Error al cargar donaciones", Toast.LENGTH_SHORT).show()
                    Log.e("VerDonacionesEmpresa", "Error: ${e.message}")
                }
            }
        }
    }
}