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
import com.example.yigo.model.DonantesAdminAdapter
import com.example.yigo.model.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaDonantesAdmin : AppCompatActivity() {

    private lateinit var rvDonantes: RecyclerView
    private var donantesList: MutableList<Map<String, Any?>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_donantes_admin)

        rvDonantes = findViewById(R.id.rvDonantesAdmin)
        rvDonantes.layoutManager = LinearLayoutManager(this)

        cargarDonantes()
    }

    private fun cargarDonantes() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val donantes = AdminUsersController.obtenerDonantes()
                withContext(Dispatchers.Main) {
                    donantesList.clear()
                    donantesList.addAll(donantes)
                    rvDonantes.adapter = DonantesAdminAdapter(
                        donantesList,
                        onEstadoClick = { usuario ->
                            alternarEstadoUsuario(usuario)
                        },
                        onEditarClick = { usuario ->
                            // Verificamos el tipo de usuario para redirigir a la Activity correspondiente
                            when (usuario.tipo) {
                                "empresa" -> {
                                    val intent = Intent(this@ListaDonantesAdmin, EditarEmpresaAdmin::class.java)
                                    intent.putExtra("userId", usuario.id)
                                    startActivity(intent)
                                }
                                "persona" -> {
                                    val intent = Intent(this@ListaDonantesAdmin, EditarPersonaAdmin::class.java)
                                    intent.putExtra("userId", usuario.id)
                                    startActivity(intent)
                                }
                                else -> {
                                    Toast.makeText(this@ListaDonantesAdmin, "Tipo de usuario no válido", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ListaDonantesAdmin, "Error al cargar donantes", Toast.LENGTH_SHORT).show()
                    Log.e("ListaDonantesAdmin", "Error: ${e.message}")
                }
            }
        }
    }

    private fun alternarEstadoUsuario(usuario: Usuario) {
        CoroutineScope(Dispatchers.IO).launch {
            val resultado = AdminUsersController.alternarEstadoUsuario(usuario.id)
            withContext(Dispatchers.Main) {
                if (resultado) {
                    Toast.makeText(this@ListaDonantesAdmin, "Estado actualizado", Toast.LENGTH_SHORT).show()
                    cargarDonantes()
                } else {
                    Toast.makeText(this@ListaDonantesAdmin, "Error al actualizar estado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}