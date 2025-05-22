package com.example.yigo.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R
import com.example.yigo.model.FoundationAdapter
import com.example.yigo.controller.FoundationController
import com.example.yigo.model.Fundacion
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SelectFoundationActivity : AppCompatActivity() {

    private lateinit var rvFundaciones: RecyclerView
    private lateinit var btnRegresar: Button
    private val fundacionesList = mutableListOf<Fundacion>()
    private lateinit var foundationAdapter: FoundationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_foundation)

        rvFundaciones = findViewById(R.id.rvFundaciones)
        btnRegresar = findViewById(R.id.btnRegresar)

        setupRecyclerView()
        loadFundaciones()

        btnRegresar.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        foundationAdapter = FoundationAdapter(
            fundaciones = fundacionesList,
            onSelectClick = { fundacion ->
                val intent = Intent()
                intent.putExtra("fundacion_id", fundacion.id)
                intent.putExtra("fundacion_nombre", fundacion.nombre_fundacion)
                setResult(RESULT_OK, intent)
                finish()
            },
            onLocationClick = { fundacion ->
                val intent = Intent(this, MapActivity::class.java)
                intent.putExtra("ubicacion_lat", fundacion.ubicacion_lat)
                intent.putExtra("ubicacion_lng", fundacion.ubicacion_lng)
                startActivity(intent)
            }

        )

        rvFundaciones.apply {
            layoutManager = LinearLayoutManager(this@SelectFoundationActivity)
            adapter = foundationAdapter
        }
    }

    private fun loadFundaciones() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val fundaciones = FoundationController.obtenerFundaciones()
                withContext(Dispatchers.Main) {
                    fundacionesList.clear()
                    fundacionesList.addAll(fundaciones)
                    foundationAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("SelectFoundation", "Error al cargar fundaciones: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SelectFoundationActivity, "Error al cargar fundaciones", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}