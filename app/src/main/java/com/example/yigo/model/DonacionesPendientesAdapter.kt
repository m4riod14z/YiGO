package com.example.yigo.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yigo.R

class DonacionesPendientesAdapter(
    private val donaciones: List<Map<String, Any?>>,
    private val onAceptarClick: (Donaciones) -> Unit,
    private val onRechazarClick: (Donaciones) -> Unit,
    private val onVerUbicacionClick: (Donaciones) -> Unit
) : RecyclerView.Adapter<DonacionesPendientesAdapter.DonacionViewHolder>() {

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
        val nombreDonante = data["nombre_donante"] as String
        val telefonoDonante = data["telefono_donante"] as String
        val tipoAlimento = data["tipo_alimento"] as String
        val nombreFundacion = data["nombre_fundacion"] as String

        holder.tvProductos.text = donacion.productos
        holder.tvEstadoDonacion.text = "Estado: ${donacion.estado}"
        holder.tvDonante.text = "Donante: $nombreDonante"
        holder.tvTelefonoDonante.text = "Teléfono: $telefonoDonante"
        holder.tvDireccion.text = "Dirección: ${donacion.direccion}"
        holder.tvTipoAlimento.text = "Tipo: $tipoAlimento"
        holder.tvDescripcion.text = donacion.descripcion
        holder.tvFundacionAsignada.text = "Fundación: $nombreFundacion"

        Glide.with(holder.itemView.context)
            .load(donacion.imagen_url)
            .placeholder(R.drawable.placeholder)
            .into(holder.ivImagenDonacion)

        // Control de visibilidad según el estado de la donación
        when (donacion.estado) {
            DonationState.Publicada.value -> {
                holder.btnAceptar.visibility = View.VISIBLE
                holder.btnRechazar.visibility = View.GONE
                holder.btnVerUbicacion.visibility = View.VISIBLE
                holder.btnEntregada.visibility = View.GONE
                holder.btnCalificar.visibility = View.GONE
            }
            DonationState.Asignada.value -> {
                holder.btnAceptar.visibility = View.VISIBLE
                holder.btnRechazar.visibility = View.VISIBLE
                holder.btnVerUbicacion.visibility = View.VISIBLE
                holder.btnEntregada.visibility = View.GONE
                holder.btnCalificar.visibility = View.GONE
            }
            DonationState.Aceptada.value -> {
                holder.btnAceptar.visibility = View.GONE
                holder.btnRechazar.visibility = View.GONE
                holder.btnVerUbicacion.visibility = View.VISIBLE
                holder.btnEntregada.visibility = View.VISIBLE
                holder.btnCalificar.visibility = View.GONE
            }
            DonationState.Entregada.value -> {
                holder.btnAceptar.visibility = View.GONE
                holder.btnRechazar.visibility = View.GONE
                holder.btnVerUbicacion.visibility = View.VISIBLE
                holder.btnEntregada.visibility = View.GONE
                holder.btnCalificar.visibility = View.VISIBLE
            }
            else -> {
                holder.btnAceptar.visibility = View.GONE
                holder.btnRechazar.visibility = View.GONE
                holder.btnVerUbicacion.visibility = View.GONE
                holder.btnEntregada.visibility = View.GONE
                holder.btnCalificar.visibility = View.GONE
            }
        }

        // Acciones de los botones
        holder.btnAceptar.setOnClickListener { onAceptarClick(donacion) }
        holder.btnRechazar.setOnClickListener { onRechazarClick(donacion) }
        holder.btnVerUbicacion.setOnClickListener { onVerUbicacionClick(donacion) }
    }

    override fun getItemCount(): Int = donaciones.size
}