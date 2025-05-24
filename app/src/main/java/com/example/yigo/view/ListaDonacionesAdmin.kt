package com.example.yigo.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R
import com.example.yigo.controller.DonationController
import com.example.yigo.model.DonacionesAdminAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaDonacionesAdmin : AppCompatActivity() {

    private lateinit var rvDonacionesAdmin: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_donaciones_admin)

        rvDonacionesAdmin = findViewById(R.id.rvDonacionesAdmin)
        rvDonacionesAdmin.layoutManager = LinearLayoutManager(this)

        cargarDonaciones()
    }

    private fun cargarDonaciones() {
        CoroutineScope(Dispatchers.IO).launch {
            val donaciones = DonationController.obtenerTodasLasDonaciones()

            withContext(Dispatchers.Main) {
                rvDonacionesAdmin.adapter = DonacionesAdminAdapter(donaciones) { donacionId, imagenUrl ->
                    // Ejecutar la eliminación en un contexto IO
                    CoroutineScope(Dispatchers.IO).launch {
                        val resultado = DonationController.eliminarDonacion(donacionId, imagenUrl)

                        withContext(Dispatchers.Main) {
                            if (resultado) {
                                Toast.makeText(this@ListaDonacionesAdmin, "Donación eliminada", Toast.LENGTH_SHORT).show()
                                cargarDonaciones()  // Recargar la lista después de la eliminación
                            } else {
                                Toast.makeText(this@ListaDonacionesAdmin, "Error al eliminar donación", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}