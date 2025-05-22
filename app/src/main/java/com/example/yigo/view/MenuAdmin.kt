package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.yigo.R
import com.example.yigo.controller.AuthController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuAdmin : AppCompatActivity() {

    private lateinit var btnDonantes: Button
    private lateinit var btnFundaciones: Button
    private lateinit var btnDonaciones: Button
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_admin)

        btnDonantes = findViewById(R.id.btnDonantes)
        btnFundaciones = findViewById(R.id.btnFundaciones)
        btnDonaciones = findViewById(R.id.btnDonaciones)
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion)

        btnDonantes.setOnClickListener {
            val intent = Intent(this, ListaDonantesAdmin::class.java)
            startActivity(intent)
        }

        btnFundaciones.setOnClickListener {
            val intent = Intent(this, ListaFundacionesAdmin::class.java)
            startActivity(intent)
        }

        btnDonaciones.setOnClickListener {
            val intent = Intent(this, ListaDonacionesAdmin::class.java)
            startActivity(intent)
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
                    Toast.makeText(this@MenuAdmin, "Vuelva pronto!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MenuAdmin, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MenuAdmin, "Error al cerrar sesión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}