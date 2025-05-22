package com.example.yigo.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yigo.R
import com.example.yigo.controller.DonationController
import com.example.yigo.model.TipoAlimento
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Donation : AppCompatActivity() {

    private lateinit var imagePreview: ImageButton
    private lateinit var direccionEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var productoEditText: EditText
    private lateinit var seleccionarFundacionButton: Button
    private lateinit var tipoAlimentoSpinner: Spinner
    private lateinit var cancelarButton: Button
    private lateinit var donarButton: Button
    private lateinit var obtenerUbicacionButton: Button

    private var imageUri: Uri? = null
    private var imagenUrl: String? = null
    private var fundacionId: String? = null
    private var tiposAlimento: MutableList<TipoAlimento> = mutableListOf()
    private var ubicacionLat: Double? = null
    private var ubicacionLng: Double? = null
    private var ubicacionAñadida = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val selectImageResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                imageUri = uri
                imagePreview.setImageURI(uri)
                Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val selectFoundationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            fundacionId = data?.getStringExtra("fundacion_id")
            val fundacionNombre = data?.getStringExtra("fundacion_nombre")

            if (fundacionId != null) {
                seleccionarFundacionButton.text = getString(R.string.fundacion_seleccionada, fundacionNombre)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donacion)

        imagePreview = findViewById(R.id.imageView10)
        direccionEditText = findViewById(R.id.direccion_donante)
        descripcionEditText = findViewById(R.id.editTextText9)
        productoEditText = findViewById(R.id.textView10)
        seleccionarFundacionButton = findViewById(R.id.buttonSeleccionarFundacion)
        tipoAlimentoSpinner = findViewById(R.id.spinner_tipo_producto)
        cancelarButton = findViewById(R.id.button9)
        donarButton = findViewById(R.id.button8)
        obtenerUbicacionButton = findViewById(R.id.btnObtenerUbicacion)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        imagePreview.setOnClickListener { openImageSelector() }
        cancelarButton.setOnClickListener { cancelarDonacion() }
        donarButton.setOnClickListener { iniciarProcesoDonacion() }
        seleccionarFundacionButton.setOnClickListener { abrirSeleccionarFundacion() }
        obtenerUbicacionButton.setOnClickListener { solicitarPermisosUbicacion() }

        cargarTiposAlimento()
    }

    private fun openImageSelector() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        selectImageResultLauncher.launch(intent)
    }


    private fun abrirSeleccionarFundacion() {
        val intent = Intent(this, SelectFoundationActivity::class.java)
        selectFoundationLauncher.launch(intent)
    }

    private fun cargarTiposAlimento() {
        CoroutineScope(Dispatchers.IO).launch {
            val tipos = DonationController.obtenerTiposAlimento()
            runOnUiThread {
                tiposAlimento.clear()
                tiposAlimento.add(TipoAlimento(0, "Selecciona un tipo de alimento"))
                tiposAlimento.addAll(tipos)

                val adapter = ArrayAdapter(
                    this@Donation,
                    android.R.layout.simple_spinner_item,
                    tiposAlimento.map { it.tipo }
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                tipoAlimentoSpinner.adapter = adapter
            }
        }
    }

    private fun solicitarPermisosUbicacion() {
        val fineLocationGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (fineLocationGranted) {
            obtenerUbicacionActual()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Mostrar una explicación antes de solicitar nuevamente
                Toast.makeText(this, "La aplicación necesita acceso a tu ubicación para continuar", Toast.LENGTH_LONG).show()
            }

            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            obtenerUbicacionActual()
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "El permiso de ubicación fue denegado permanentemente. Actívalo en los ajustes.", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val REQUEST_CHECK_SETTINGS = 1001

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == RESULT_OK) {
                Log.d("Donation", "Ubicación activada por el usuario.")
                obtenerUbicacionActual()
            } else {
                Toast.makeText(this, "La ubicación no fue activada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Donation", "Permiso de ubicación no concedido")
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).build()

        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .build()

        val settingsClient: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = settingsClient.checkLocationSettings(settingsRequest)

        task.addOnSuccessListener {
            Log.d("Donation", "El GPS está activado. Intentando obtener ubicación...")

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    ubicacionLat = location.latitude
                    ubicacionLng = location.longitude
                    ubicacionAñadida = true
                    Log.d("Donation", "Ubicación obtenida: Lat=${ubicacionLat}, Lng=${ubicacionLng}")
                    Toast.makeText(this, "Ubicación obtenida", Toast.LENGTH_SHORT).show()
                } else {
                    Log.w("Donation", "Última ubicación es null. Intentando obtener una nueva ubicación...")
                    // Solicitar una nueva ubicación activamente
                    solicitarNuevaUbicacion(locationRequest)
                }
            }.addOnFailureListener { exception ->
                Log.e("Donation", "Error al obtener la ubicación: ${exception.message}")
                Toast.makeText(this, "Error al obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    exception.startResolutionForResult(this, REQUEST_CHECK_SETTINGS)
                } catch (sendEx: Exception) {
                    Log.e("Donation", "Error al activar la ubicación: ${sendEx.message}")
                }
            } else {
                Toast.makeText(this, "No se pudo activar la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var locationCallback: LocationCallback

    private fun solicitarNuevaUbicacion(locationRequest: LocationRequest) {
        // Verificar permisos antes de solicitar la ubicación
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("Donation", "Permiso de ubicación no concedido")
            Toast.makeText(this, "Permiso de ubicación no concedido", Toast.LENGTH_SHORT).show()
            return
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val location = locationResult.lastLocation
                if (location != null) {
                    ubicacionLat = location.latitude
                    ubicacionLng = location.longitude
                    Log.d("Donation", "Nueva ubicación obtenida: Lat=${ubicacionLat}, Lng=${ubicacionLng}")
                    Toast.makeText(this@Donation, "Ubicación actualizada", Toast.LENGTH_SHORT).show()

                    // Importante: Detener las actualizaciones para evitar múltiples llamadas
                    fusedLocationClient.removeLocationUpdates(this)
                } else {
                    Log.e("Donation", "No se pudo obtener la ubicación (LocationResult vacío)")
                    Toast.makeText(this@Donation, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    private fun iniciarProcesoDonacion() {
        val direccion = direccionEditText.text.toString().trim()
        val descripcion = descripcionEditText.text.toString().trim()
        val productos = productoEditText.text.toString().trim()
        val tipoIndex = tipoAlimentoSpinner.selectedItemPosition

        if (direccion.isEmpty() || descripcion.isEmpty() || productos.isEmpty() || tipoIndex == 0) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (!ubicacionAñadida) {
            Toast.makeText(this, "Primero debes añadir la ubicación", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageUri == null) {
            Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Subiendo imagen...", Toast.LENGTH_SHORT).show()

        CoroutineScope(Dispatchers.IO).launch {
            val uploadedImageUrl = subirImagenDonacion(imageUri!!)

            if (uploadedImageUrl != null) {
                imagenUrl = uploadedImageUrl

                val imagenUrlFinal = imagenUrl ?: ""
                crearDonacion(
                    direccion = direccion,
                    descripcion = descripcion,
                    productos = productos,
                    tipoAlimentoId = tiposAlimento[tipoIndex].id,
                    imagenUrl = imagenUrlFinal,
                    id_fundacion = fundacionId
                )

            } else {
                runOnUiThread {
                    Toast.makeText(this@Donation, "Error al subir la imagen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private suspend fun subirImagenDonacion(uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val fileName = "donation-${UUID.randomUUID()}.jpg"
                val inputStream = contentResolver.openInputStream(uri)
                val fileBytes = inputStream?.readBytes()
                inputStream?.close()

                if (fileBytes != null) {
                    DonationController.subirImagenDonacion(fileName, fileBytes)
                } else {
                    null
                }
            } catch (e: Exception) {
                Log.e("Donation", "Error al subir imagen: ${e.message}", e)
                null
            }
        }
    }

    private suspend fun crearDonacion(
        direccion: String,
        descripcion: String,
        productos: String,
        tipoAlimentoId: Int,
        imagenUrl: String,
        id_fundacion: String? = null
    ) {

        val donacionId = DonationController.crearDonacionConDatos(
            direccion = direccion,
            descripcion = descripcion,
            productos = productos,
            tipoAlimentoId = tipoAlimentoId,
            imagenUrl = imagenUrl,
            ubicacionLat = ubicacionLat?: 0.0,
            ubicacionLng = ubicacionLng?: 0.0,
            fundacionId = id_fundacion
        )

        runOnUiThread {
            if (donacionId != null) {
                Toast.makeText(this@Donation, "Donación creada correctamente", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@Donation, "Error al crear donación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cancelarDonacion() {
        Toast.makeText(this, "Donación cancelada", Toast.LENGTH_SHORT).show()
        finish()
    }
}