package com.example.yigo.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.yigo.R
import android.content.Intent
import android.widget.Button
import android.widget.Toast
import com.example.yigo.controller.AuthController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuFundacionActivity : AppCompatActivity() {

    private lateinit var btnDonaciones: Button
    private lateinit var btnVerPerfil: Button
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_fundacion)

        btnDonaciones = findViewById(R.id.btnDonaciones)
        btnVerPerfil = findViewById(R.id.btnVerPerfil)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        btnDonaciones.setOnClickListener {
            val intent = Intent(this, DonacionesFundacionActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnVerPerfil.setOnClickListener {
            val intent = Intent(this, VerPerfilFundacion::class.java)
            startActivity(intent)
            finish()
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cerrarSesion() {
        CoroutineScope(Dispatchers.IO).launch {
            val resultado = AuthController.cerrarSesion()
            withContext(Dispatchers.Main) {
                if (resultado) {
                    Toast.makeText(this@MenuFundacionActivity, "Vuelva pronto!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MenuFundacionActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MenuFundacionActivity, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}