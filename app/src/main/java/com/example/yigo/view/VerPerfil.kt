package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yigo.R
import com.example.yigo.controller.ProfileController
import com.example.yigo.model.SupabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VerPerfil : AppCompatActivity() {

    private lateinit var tvCorreo: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvNombre: TextView
    private lateinit var tvApellido: TextView
    private lateinit var btnEditarPerfil: Button

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)

        // Inicializar vistas
        tvCorreo = findViewById(R.id.tvCorreo)
        tvTelefono = findViewById(R.id.tvTelefono)
        tvNombre = findViewById(R.id.tvNombre)
        tvApellido = findViewById(R.id.tvApellido)
        btnEditarPerfil = findViewById(R.id.btn_Editarperfil)

        // Obtener el userId del usuario actual
        userId = SupabaseService.auth.currentUserOrNull()?.id

        if (userId != null) {
            cargarDatosPersona(userId!!)
        } else {
            Toast.makeText(this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show()
            Log.e("VerPerfil", "userId es null")
        }

        btnEditarPerfil.setOnClickListener {
            if (userId != null) {
                val intent = Intent(this, EditarPerfil::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error al obtener el usuario actual", Toast.LENGTH_SHORT).show()
                Log.e("VerPerfil", "userId es null al intentar editar perfil")
            }
        }
    }

    private fun cargarDatosPersona(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val (usuario, persona) = ProfileController.obtenerDatosPersona(userId)

            withContext(Dispatchers.Main) {
                if (usuario != null && persona != null) {
                    tvCorreo.text = usuario.correo
                    tvTelefono.text = usuario.telefono
                    tvNombre.text = persona.nombre
                    tvApellido.text = persona.apellido
                } else {
                    Toast.makeText(this@VerPerfil, "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show()
                    Log.e("VerPerfil", "Usuario o Persona nulo")
                }
            }
        }
    }
}