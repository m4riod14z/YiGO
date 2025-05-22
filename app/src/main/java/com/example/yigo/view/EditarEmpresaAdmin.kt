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

class EditarEmpresaAdmin : AppCompatActivity() {

    private lateinit var etRazonSocial: EditText
    private lateinit var etNit: EditText
    private lateinit var etTelefono: EditText
    private lateinit var etCorreo: EditText
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_empresa_admin)

        etRazonSocial = findViewById(R.id.etRazonSocial)
        etNit = findViewById(R.id.etNit)
        etTelefono = findViewById(R.id.etTelefonoEmpresa)
        etCorreo = findViewById(R.id.etCorreoEmpresa)
        btnGuardar = findViewById(R.id.btn_GuardarperfilEmpresa)
        btnCancelar = findViewById(R.id.btn_CencelarperfilEmpresa)

        userId = intent.getStringExtra("userId")

        if (userId != null) {
            cargarDatosEmpresa(userId!!)
        } else {
            Toast.makeText(this, "ID del usuario no encontrado", Toast.LENGTH_SHORT).show()
            Log.e("EditarEmpresaAdmin", "userId es null")
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

    private fun cargarDatosEmpresa(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val (usuario, empresa) = ProfileController.obtenerDatosEmpresa(userId)

                withContext(Dispatchers.Main) {
                    if (usuario != null && empresa != null) {
                        etTelefono.setText(usuario.telefono)
                        etCorreo.setText(usuario.correo)
                        etRazonSocial.setText(empresa.razon_social)
                        etNit.setText(empresa.nit)
                    } else {
                        Toast.makeText(this@EditarEmpresaAdmin, "Datos no encontrados", Toast.LENGTH_SHORT).show()
                        Log.e("EditarEmpresaAdmin", "Datos del usuario o empresa son null")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditarEmpresaAdmin, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
                    Log.e("EditarEmpresaAdmin", "Error al cargar los datos: ${e.message}")
                }
            }
        }
    }

    private fun validarCampos(): Boolean {
        val nit = etNit.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        if (!nit.matches(Regex("\\d{9}"))) {
            Toast.makeText(this, "El NIT debe tener exactamente 9 dígitos", Toast.LENGTH_SHORT).show()
            etNit.requestFocus()
            return false
        }

        if (!telefono.matches(Regex("\\d{10}"))) {
            Toast.makeText(this, "El teléfono debe tener exactamente 10 dígitos", Toast.LENGTH_SHORT).show()
            etTelefono.requestFocus()
            return false
        }

        return true
    }

    private fun guardarCambios() {
        val razonSocial = etRazonSocial.text.toString().trim()
        val nit = etNit.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()

        if (razonSocial.isEmpty() || nit.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        userId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val resultado = ProfileController.editarEmpresa(id, razonSocial, nit, telefono)

                withContext(Dispatchers.Main) {
                    if (resultado) {
                        Toast.makeText(this@EditarEmpresaAdmin, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditarEmpresaAdmin, "Error al actualizar datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}