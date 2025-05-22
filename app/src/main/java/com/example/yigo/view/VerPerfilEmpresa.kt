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

class VerPerfilEmpresa : AppCompatActivity() {

    private lateinit var tvCorreo: TextView
    private lateinit var tvTelefono: TextView
    private lateinit var tvRazonSocial: TextView
    private lateinit var tvNit: TextView
    private lateinit var btnEditarPerfil: Button

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil_empresa)

        // Inicializar vistas
        tvCorreo = findViewById(R.id.tvCorreoEmpresa)
        tvTelefono = findViewById(R.id.tvTelefonoEmpresa)
        tvRazonSocial = findViewById(R.id.tvRazonSocialEmpresa)
        tvNit = findViewById(R.id.tvNitEmpresa)
        btnEditarPerfil = findViewById(R.id.btn_EditarperfilEmpresa)

        userId = SupabaseService.auth.currentUserOrNull()?.id

        if (userId != null) {
            cargarDatosEmpresa(userId!!)
        } else {
            Toast.makeText(this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show()
            Log.e("VerPerfilEmpresa", "userId es null")
        }

        btnEditarPerfil.setOnClickListener {
            val intent = Intent(this, EditarPerfilEmpresa::class.java)
            startActivity(intent)
        }
    }

    private fun cargarDatosEmpresa(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val (usuario, empresa) = ProfileController.obtenerDatosEmpresa(userId)

            withContext(Dispatchers.Main) {
                if (usuario != null && empresa != null) {
                    tvCorreo.text = usuario.correo
                    tvTelefono.text = usuario.telefono
                    tvRazonSocial.text = empresa.razon_social
                    tvNit.text = empresa.nit
                } else {
                    Toast.makeText(this@VerPerfilEmpresa, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}