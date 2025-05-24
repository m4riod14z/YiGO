package com.example.yigo.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yigo.R
import com.example.yigo.controller.ProfileController
import com.example.yigo.model.SupabaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarPerfilFundacion : AppCompatActivity() {

    private lateinit var etCorreoFundacion: EditText
    private lateinit var etNombreFundacion: EditText
    private lateinit var etDireccion: EditText
    private lateinit var etNecesidades: EditText
    private lateinit var etRepresentante: EditText
    private lateinit var etTelefonoRepresentante: EditText
    private lateinit var btnGuardarPerfilFundacion: Button
    private lateinit var btnCancelarPerfilFundacion: Button

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil_fundacion)

        etCorreoFundacion = findViewById(R.id.etCorreoFundacion)
        etNombreFundacion = findViewById(R.id.etNombreFundacion)
        etDireccion = findViewById(R.id.etDireccion)
        etNecesidades = findViewById(R.id.etNecesidades)
        etRepresentante = findViewById(R.id.etRepresentante)
        etTelefonoRepresentante = findViewById(R.id.etTelefonoRepresentante)
        btnGuardarPerfilFundacion = findViewById(R.id.btnGuardarFundacion)
        btnCancelarPerfilFundacion = findViewById(R.id.btnCancelarFundacion)

        userId = SupabaseService.auth.currentUserOrNull()?.id

        if (userId != null) {
            cargarDatosFundacion(userId!!)
        } else {
            Toast.makeText(this, "Error al obtener el usuario actual", Toast.LENGTH_SHORT).show()
            Log.e("EditarPerfilFundacion", "userId es null")
        }

        btnGuardarPerfilFundacion.setOnClickListener {
            if (validarCampos()) {
                guardarCambios()
            }
        }

        btnCancelarPerfilFundacion.setOnClickListener {
            finish()
        }
    }

    private fun cargarDatosFundacion(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val (usuario, fundacion) = ProfileController.obtenerDatosFundacion(userId)

            withContext(Dispatchers.Main) {
                if (usuario != null && fundacion != null) {
                    etCorreoFundacion.setText(usuario.correo)
                    etNombreFundacion.setText(fundacion.nombre_fundacion)
                    etDireccion.setText(fundacion.direccion)
                    etNecesidades.setText(fundacion.necesidades)
                    etRepresentante.setText(fundacion.nombre_representante)
                    etTelefonoRepresentante.setText(fundacion.telefono_representante)
                } else {
                    Toast.makeText(this@EditarPerfilFundacion, "Error al cargar datos de la fundación", Toast.LENGTH_SHORT).show()
                    Log.e("EditarPerfilFundacion", "Datos del usuario o fundación son null")
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

        if (nombre.isEmpty()) {
            mostrarError("El campo 'Nombre Fundación' no puede estar vacío", etNombreFundacion)
            return false
        }

        if (direccion.isEmpty()) {
            mostrarError("El campo 'Dirección' no puede estar vacío", etDireccion)
            return false
        }

        if (necesidades.isEmpty()) {
            mostrarError("El campo 'Necesidades' no puede estar vacío", etNecesidades)
            return false
        }

        if (representante.isEmpty()) {
            mostrarError("El campo 'Nombre del Representante' no puede estar vacío", etRepresentante)
            return false
        }

        if (telefono.isEmpty()) {
            mostrarError("El campo 'Teléfono Representante' no puede estar vacío", etTelefonoRepresentante)
            return false
        }

        if (!telefono.matches(Regex("\\d{10}"))) {
            mostrarError("El teléfono debe tener exactamente 10 dígitos", etTelefonoRepresentante)
            return false
        }

        return true
    }

    private fun mostrarError(mensaje: String, campo: EditText) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        campo.requestFocus()
    }

    private fun guardarCambios() {
        val nombre = etNombreFundacion.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val necesidades = etNecesidades.text.toString().trim()
        val representante = etRepresentante.text.toString().trim()
        val telefono = etTelefonoRepresentante.text.toString().trim()

        userId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val resultado = ProfileController.editarFundacion(id, nombre, necesidades, direccion, representante, telefono)

                withContext(Dispatchers.Main) {
                    if (resultado) {
                        Toast.makeText(this@EditarPerfilFundacion, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditarPerfilFundacion, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}