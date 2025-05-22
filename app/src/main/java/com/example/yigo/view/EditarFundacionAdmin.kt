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

class EditarFundacionAdmin : AppCompatActivity() {

    private lateinit var etNombreFundacion: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etNecesidades: EditText
    private lateinit var etRepresentante: EditText
    private lateinit var etTelefonoRepresentante: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_fundacion_admin)

        etNombreFundacion = findViewById(R.id.etNombreFundacion)
        etDireccion = findViewById(R.id.etDireccion)
        etNecesidades = findViewById(R.id.etNecesidades)
        etRepresentante = findViewById(R.id.etRepresentante)
        etTelefonoRepresentante = findViewById(R.id.etTelefonoRepresentante)
        etCorreo = findViewById(R.id.etCorreoFundacion)
        btnGuardar = findViewById(R.id.btnGuardarFundacion)
        btnCancelar = findViewById(R.id.btnCancelarFundacion)

        userId = intent.getStringExtra("userId")

        if (userId != null) {
            cargarDatosFundacion(userId!!)
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

    private fun cargarDatosFundacion(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (usuario, fundacion) = ProfileController.obtenerDatosFundacion(userId)
                withContext(Dispatchers.Main) {
                    if (fundacion != null) {
                        etNombreFundacion.setText(fundacion.nombre_fundacion)
                        etDireccion.setText(fundacion.direccion)
                        etNecesidades.setText(fundacion.necesidades)
                        etRepresentante.setText(fundacion.nombre_representante)
                        etTelefonoRepresentante.setText(fundacion.telefono_representante)
                        etCorreo.setText(usuario?.correo) // Solo lectura
                    } else {
                        mostrarError("Error al cargar los datos de la fundación")
                    }
                }
            } catch (e: Exception) {
                Log.e("EditarFundacionAdmin", "Error al cargar los datos: ${e.message}")
                withContext(Dispatchers.Main) {
                    mostrarError("Error al cargar los datos")
                }
            }
        }
    }

    private fun validarCampos(): Boolean {
        val nombre = etNombreFundacion.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val necesidades = etNecesidades.text.toString().trim()
        val representante = etRepresentante.text.toString().trim()
        val telefono = etTelefonoRepresentante.text.toString().trim()

        // Verificar si algún campo está vacío
        if (nombre.isEmpty() || direccion.isEmpty() || necesidades.isEmpty() || representante.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validar que el teléfono tenga exactamente 10 dígitos
        if (!telefono.matches(Regex("\\d{10}"))) {
            Toast.makeText(this, "El teléfono debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show()
            etTelefonoRepresentante.requestFocus()
            return false
        }

        return true
    }

    private fun guardarCambios() {
        val nombre = etNombreFundacion.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val necesidades = etNecesidades.text.toString().trim()
        val representante = etRepresentante.text.toString().trim()
        val telefono = etTelefonoRepresentante.text.toString().trim()

        userId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val resultado = ProfileController.editarFundacion(
                        id = id,
                        nombre = nombre,
                        necesidades = necesidades,
                        direccion = direccion,
                        nombreRepresentante = representante,
                        telefonoRepresentante = telefono
                    )

                    withContext(Dispatchers.Main) {
                        if (resultado) {
                            Toast.makeText(
                                this@EditarFundacionAdmin,
                                "Datos actualizados correctamente",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            mostrarError("Error al actualizar los datos")
                        }
                    }

                } catch (e: Exception) {
                    Log.e("EditarFundacionAdmin", "Error al guardar los datos: ${e.message}")
                    withContext(Dispatchers.Main) {
                        mostrarError("Error al guardar los datos")
                    }
                }
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}