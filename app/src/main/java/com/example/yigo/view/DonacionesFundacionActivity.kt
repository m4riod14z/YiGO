package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.yigo.R

class DonacionesFundacionActivity : AppCompatActivity() {

    private lateinit var btnDonacionesPendientesPersonas: Button
    private lateinit var btnDonacionesCompletadasPersonas: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_donaciones_personas_fundacion)

        btnDonacionesPendientesPersonas = findViewById(R.id.btnDonacionesPendientesPersonas)
        btnDonacionesCompletadasPersonas = findViewById(R.id.btnDonacionesCompletadasPersonas)
        btnBack = findViewById(R.id.btnBack)

        btnDonacionesPendientesPersonas.setOnClickListener {
            val intent = Intent(this, DonacionesPendientesActivity::class.java)
            startActivity(intent)
        }

        btnDonacionesCompletadasPersonas.setOnClickListener {
            val intent = Intent(this, DonacionesCompletadasActivity::class.java)
            startActivity(intent)
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MenuFundacionActivity::class.java)
            startActivity(intent)
        }
    }
}