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

class VerPerfilFundacion : AppCompatActivity() {

    private lateinit var tvCorreoFundacion: TextView
    private lateinit var tvNombreFundacion: TextView
    private lateinit var tvDireccion: TextView
    private lateinit var tvNecesidades: TextView
    private lateinit var tvRepresentante: TextView
    private lateinit var tvTelefonoRepresentante: TextView
    private lateinit var btnEditarPerfilFundacion: Button
    private lateinit var btnRegresar: Button

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil_fundacion)

        tvCorreoFundacion = findViewById(R.id.tvCorreoFundacion)
        tvNombreFundacion = findViewById(R.id.tvNombreFundacion)
        tvDireccion = findViewById(R.id.tvDireccion)
        tvNecesidades = findViewById(R.id.tvNecesidades)
        tvRepresentante = findViewById(R.id.tvRepresentante)
        tvTelefonoRepresentante = findViewById(R.id.tvTelefonoRepresentante)
        btnEditarPerfilFundacion = findViewById(R.id.btnEditarPerfilFundacion)
        btnRegresar = findViewById(R.id.btnRegresar)

        userId = SupabaseService.auth.currentUserOrNull()?.id

        if (userId != null) {
            cargarDatosFundacion(userId!!)
        } else {
            Toast.makeText(this, "Error al obtener el usuario actual", Toast.LENGTH_SHORT).show()
            Log.e("VerPerfilFundacion", "userId es null")
        }

        btnEditarPerfilFundacion.setOnClickListener {
            startActivity(Intent(this, EditarPerfilFundacion::class.java))
        }

        btnRegresar.setOnClickListener {
            val intent = Intent(this, MenuFundacionActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun cargarDatosFundacion(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val (usuario, fundacion) = ProfileController.obtenerDatosFundacion(userId)

            withContext(Dispatchers.Main) {
                if (usuario != null && fundacion != null) {
                    tvCorreoFundacion.text = usuario.correo
                    tvNombreFundacion.text = fundacion.nombre_fundacion
                    tvDireccion.text = fundacion.direccion
                    tvNecesidades.text = fundacion.necesidades
                    tvRepresentante.text = fundacion.nombre_representante
                    tvTelefonoRepresentante.text = fundacion.telefono_representante
                } else {
                    Toast.makeText(this@VerPerfilFundacion, "Error al cargar los datos de la fundación", Toast.LENGTH_SHORT).show()
                    Log.e("VerPerfilFundacion", "Usuario o Fundación nulo")
                }
            }
        }
    }
}