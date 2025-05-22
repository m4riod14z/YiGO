package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yigo.R
import com.example.yigo.controller.AuthController
import com.example.yigo.model.SupabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Inicio_sesion_usuario : AppCompatActivity() {

    private var userId: String? = null
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio_sesion_usuario)

        val btnDonar: Button = findViewById(R.id.button_donar)
        val btnVerPerfil: Button = findViewById(R.id.button_ver_perfil)
        val btnVerDonaciones: Button = findViewById(R.id.button_editar_perfil)
        val btnCerrarSesion: Button = findViewById(R.id.button_cerrar_sesion)

        // Obtener el User ID al iniciar la actividad
        userId = SupabaseService.auth.currentUserOrNull()?.id
        Log.d("InicioSesionUsuario", "User ID obtenido: $userId")

        if (userId != null) {
            obtenerTipoUsuario(userId!!)
        } else {
            Toast.makeText(this, "Error al obtener el usuario actual", Toast.LENGTH_SHORT).show()
            Log.e("InicioSesionUsuario", "Usuario no autenticado")
        }

        btnDonar.setOnClickListener {
            val intent = Intent(this, Donation::class.java)
            startActivity(intent)
        }

        btnVerPerfil.setOnClickListener {
            if (userType != null) {
                verificarTipoUsuario()
            } else {
                Toast.makeText(this, "Cargando información del usuario...", Toast.LENGTH_SHORT).show()
                Log.e("InicioSesionUsuario", "Intento de acceso al perfil sin cargar el tipo de usuario")
            }
        }

        btnVerDonaciones.setOnClickListener {
            when (userType) {
                "persona" -> startActivity(Intent(this, VerDonacionesUsuario::class.java))
                "empresa" -> startActivity(Intent(this, VerDonacionesEmpresa::class.java))
                else -> Toast.makeText(this, "Tipo de usuario no válido", Toast.LENGTH_SHORT).show()
            }
        }
        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun obtenerTipoUsuario(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val tipo = AuthController.obtenerTipoUsuario(userId)
                Log.d("InicioSesionUsuario", "Tipo de usuario obtenido: $tipo")

                withContext(Dispatchers.Main) {
                    userType = tipo

                    if (userType == null) {
                        Toast.makeText(this@Inicio_sesion_usuario, "Error al obtener tipo de usuario", Toast.LENGTH_SHORT).show()
                        Log.e("InicioSesionUsuario", "Tipo de usuario es null")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Inicio_sesion_usuario, "Error al obtener tipo de usuario", Toast.LENGTH_SHORT).show()
                }
                Log.e("InicioSesionUsuario", "Error al obtener tipo de usuario: ${e.message}")
            }
        }
    }

    private fun verificarTipoUsuario() {
        when (userType) {
            "persona" -> {
                val intent = Intent(this, VerPerfil::class.java)
                startActivity(intent)
            }
            "empresa" -> {
                val intent = Intent(this, VerPerfilEmpresa::class.java)
                startActivity(intent)
            }
            else -> {
                Toast.makeText(this, "Tipo de usuario no válido o no cargado", Toast.LENGTH_SHORT).show()
                Log.e("InicioSesionUsuario", "Tipo de usuario no válido o no cargado: $userType")
            }
        }
    }

    private fun cerrarSesion() {
        CoroutineScope(Dispatchers.IO).launch {
            val resultado = AuthController.cerrarSesion()
            withContext(Dispatchers.Main) {
                if (resultado) {
                    Toast.makeText(this@Inicio_sesion_usuario, "Vuelva pronto!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Inicio_sesion_usuario, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@Inicio_sesion_usuario, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}