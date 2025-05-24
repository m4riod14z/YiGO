package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.yigo.R
import com.example.yigo.controller.AuthController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.editTextTextEmailAddress3)
        passwordEditText = findViewById(R.id.editTextTextPassword4)
        loginButton = findViewById(R.id.button7)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                iniciarSesion(email, password)
            } else {
                Toast.makeText(this, "Por favor, ingresa ambos campos", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun iniciarSesion(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val (exito, mensaje, tipo) = AuthController.iniciarSesion(email, password)

            runOnUiThread {
                Toast.makeText(this@LoginActivity, mensaje, Toast.LENGTH_SHORT).show()

                if (exito) {
                    when (tipo) {
                        "persona", "empresa" -> {
                            val intent = Intent(this@LoginActivity, Inicio_sesion_usuario::class.java)
                            intent.putExtra("user_type", tipo)  // Enviar el tipo de usuario
                            startActivity(intent)
                            finish()
                        }
                        "fundacion" -> {
                            // Redirigir a actividad para fundaciones
                            val intent = Intent(this@LoginActivity, MenuFundacionActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        "admin" -> {
                            // Redirigir a actividad para administradores
                            val intent = Intent(this@LoginActivity, MenuAdmin::class.java)
                            intent.putExtra("user_type", tipo)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }
}