package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R
import com.example.yigo.controller.AdminUsersController
import com.example.yigo.model.FundacionAdminAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaFundacionesAdmin : AppCompatActivity() {

    private lateinit var rvFundacionesAdmin: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_fundaciones_admin)

        rvFundacionesAdmin = findViewById(R.id.rvFundacionesAdmin)
        rvFundacionesAdmin.layoutManager = LinearLayoutManager(this)

        cargarFundaciones()
    }

    private fun cargarFundaciones() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fundaciones = AdminUsersController.obtenerFundaciones()
                withContext(Dispatchers.Main) {
                    if (fundaciones.isEmpty()) {
                        Toast.makeText(this@ListaFundacionesAdmin, "No hay fundaciones disponibles", Toast.LENGTH_SHORT).show()
                    } else {
                        val adapter = FundacionAdminAdapter(fundaciones,
                            onEstadoCambiado = {
                                cargarFundaciones() // Recargar lista al cambiar estado
                            },
                            onEditarClick = { usuario ->
                                val intent = Intent(this@ListaFundacionesAdmin, EditarFundacionAdmin::class.java)
                                intent.putExtra("userId", usuario.id)
                                startActivity(intent)
                            }
                        )
                        rvFundacionesAdmin.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Log.e("ListaFundacionesAdmin", "Error al cargar fundaciones: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListaFundacionesAdmin, "Error al cargar fundaciones", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}