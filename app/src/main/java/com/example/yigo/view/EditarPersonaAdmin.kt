package com.example.yigo.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yigo.R
import com.example.yigo.controller.ProfileController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarPersonaAdmin : AppCompatActivity() {

    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etTelefono = findViewById(R.id.etTelefono)
        etCorreo = findViewById(R.id.etCorreo)
        btnGuardar = findViewById(R.id.btn_Guardarperfil)
        btnCancelar = findViewById(R.id.btn_Cencelarperfil)

        // Obtener el userId del Intent
        userId = intent.getStringExtra("userId")

        if (userId != null) {
            cargarDatosPersona(userId!!)
        } else {
            Toast.makeText(this, "Error al obtener el usuario", Toast.LENGTH_SHORT).show()
            Log.e("EditarPersonaAdmin", "userId es null")
        }

        btnGuardar.setOnClickListener {
            if (validarCampos()) {
                guardarCambios()
            }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosPersona(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (usuario, persona) = ProfileController.obtenerDatosPersona(userId)

                withContext(Dispatchers.Main) {
                    if (usuario != null && persona != null) {
                        etNombre.setText(persona.nombre)
                        etApellido.setText(persona.apellido)
                        etTelefono.setText(usuario.telefono)
                        etCorreo.setText(usuario.correo)
                    } else {
                        Toast.makeText(this@EditarPersonaAdmin, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
                        Log.e("EditarPersonaAdmin", "Datos del usuario o persona son null")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarPersonaAdmin, "Error al cargar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("EditarPersonaAdmin", "Error al cargar datos de persona: ${e.message}")
                }
            }
        }
    }

    private fun validarCampos(): Boolean {
        val telefono = etTelefono.text.toString().trim()

        if (!telefono.matches(Regex("\\d{10}"))) {
            Toast.makeText(this, "El teléfono debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show()
            etTelefono.requestFocus()
            return false
        }

        return true
    }

    private fun guardarCambios() {
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        userId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val resultado = ProfileController.editarPersona(id, nombre, apellido, telefono)

                withContext(Dispatchers.Main) {
                    if (resultado) {
                        Toast.makeText(this@EditarPersonaAdmin, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditarPersonaAdmin, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}