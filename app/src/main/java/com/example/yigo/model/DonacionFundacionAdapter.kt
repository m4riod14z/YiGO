package com.example.yigo.model

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yigo.R

class DonacionFundacionAdapter(
    private val donaciones: List<Map<String, Any?>>,
    private val onAcceptClick: (Donaciones) -> Unit,
    private val onRejectClick: (Donaciones) -> Unit,
    private val onDeliverClick: (Donaciones) -> Unit,
    private val onCalificarClick: (Donaciones) -> Unit
) : RecyclerView.Adapter<DonacionFundacionAdapter.DonacionViewHolder>() {

    inner class DonacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductos: TextView = view.findViewById(R.id.tvProductos)
        val tvEstadoDonacion: TextView = view.findViewById(R.id.tvEstadoDonacion)
        val tvDonante: TextView = view.findViewById(R.id.tvDonante)
        val tvTelefonoDonante: TextView = view.findViewById(R.id.tvTelefonoDonante)
        val tvFundacionAsignada: TextView = view.findViewById(R.id.tvFundacionAsignada)
        val tvDireccion: TextView = view.findViewById(R.id.tvDireccion)
        val tvTipoAlimento: TextView = view.findViewById(R.id.tvTipoAlimento)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val ivImagenDonacion: ImageView = view.findViewById(R.id.ivImagenDonacion)

        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)
        val btnRechazar: Button = view.findViewById(R.id.btnRechazar)
        val btnEntregada: Button = view.findViewById(R.id.btnEntregada)
        val btnCalificar: Button = view.findViewById(R.id.btnCalificar)
        val btnVerUbicacion: Button = view.findViewById(R.id.btnVerUbicacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donacion_fundacion, parent, false)
        return DonacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonacionViewHolder, position: Int) {
        val data = donaciones[position]
        val donacion = data["donacion"] as Donaciones
        val nombreDonante = data["nombre_donante"] as? String ?: "No disponible"
        val telefonoDonante = data["telefono_donante"] as? String ?: "No disponible"
        val nombreFundacion = data["nombre_fundacion"] as? String ?: "No asignada"
        val tipoAlimento = data["tipo_alimento"] as? String ?: "No disponible"

        // Cargar imagen
        Glide.with(holder.itemView.context)
            .load(donacion.imagen_url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.ivImagenDonacion)

        // Asignar valores
        holder.tvProductos.text = donacion.productos
        holder.tvEstadoDonacion.text = "Estado: ${donacion.estado}"
        holder.tvDonante.text = "Donante: $nombreDonante"
        holder.tvTelefonoDonante.text = "Teléfono: $telefonoDonante"
        holder.tvFundacionAsignada.text = "Fundación: $nombreFundacion"
        holder.tvDireccion.text = "Dirección: ${donacion.direccion}"
        holder.tvTipoAlimento.text = "Tipo de alimento: $tipoAlimento"
        holder.tvDescripcion.text = donacion.descripcion

        // Control de visibilidad de botones según estado
        when (donacion.estado) {
            "publicada" -> {
                holder.btnAceptar.visibility = View.VISIBLE
                holder.btnRechazar.visibility = View.GONE
                holder.btnEntregada.visibility = View.GONE
                holder.btnCalificar.visibility = View.GONE
            }
            "asignada" -> {
                holder.btnAceptar.visibility = View.VISIBLE
                holder.btnRechazar.visibility = View.VISIBLE
                holder.btnEntregada.visibility = View.GONE
                holder.btnCalificar.visibility = View.GONE
            }
            "aceptada" -> {
                holder.btnAceptar.visibility = View.GONE
                holder.btnRechazar.visibility = View.GONE
                holder.btnEntregada.visibility = View.VISIBLE
                holder.btnCalificar.visibility = View.GONE
            }
            "entregada" -> {
                holder.btnAceptar.visibility = View.GONE
                holder.btnRechazar.visibility = View.GONE
                holder.btnEntregada.visibility = View.GONE
                holder.btnCalificar.visibility = View.VISIBLE
            }
        }

        // Botón Ubicación
        holder.btnVerUbicacion.setOnClickListener {
            val lat = donacion.ubicacion_lat
            val lng = donacion.ubicacion_lng

            if (lat != null && lng != null) {
                val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng(Donación)")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                holder.itemView.context.startActivity(mapIntent)
            } else {
                Toast.makeText(holder.itemView.context, "Ubicación no disponible", Toast.LENGTH_SHORT).show()
            }
        }

        // Acciones de los botones
        holder.btnAceptar.setOnClickListener {
            onAcceptClick(donacion)
        }

        holder.btnRechazar.setOnClickListener {
            onRejectClick(donacion)
        }

        holder.btnEntregada.setOnClickListener {
            onDeliverClick(donacion)
        }

        holder.btnCalificar.setOnClickListener {
            onCalificarClick(donacion)
        }
    }

    override fun getItemCount(): Int = donaciones.size
}