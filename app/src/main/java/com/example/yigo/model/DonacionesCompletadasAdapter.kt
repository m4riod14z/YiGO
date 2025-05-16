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

class DonacionesCompletadasAdapter(
    private val donaciones: List<Map<String, Any?>>,
    private val onEntregadaClick: (Donaciones) -> Unit,
    private val onCalificarClick: (Donaciones) -> Unit,
    private val onVerUbicacionClick: (Donaciones) -> Unit
) : RecyclerView.Adapter<DonacionesCompletadasAdapter.DonacionViewHolder>() {

    inner class DonacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductos: TextView = view.findViewById(R.id.tvProductos)
        val tvEstadoDonacion: TextView = view.findViewById(R.id.tvEstadoDonacion)
        val tvDonante: TextView = view.findViewById(R.id.tvDonante)
        val tvTelefonoDonante: TextView = view.findViewById(R.id.tvTelefonoDonante)
        val tvDireccion: TextView = view.findViewById(R.id.tvDireccion)
        val tvTipoAlimento: TextView = view.findViewById(R.id.tvTipoAlimento)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val tvFundacionAsignada: TextView = view.findViewById(R.id.tvFundacionAsignada)
        val ivImagenDonacion: ImageView = view.findViewById(R.id.ivImagenDonacion)
        val btnVerUbicacion: Button = view.findViewById(R.id.btnVerUbicacion)
        val btnEntregada: Button = view.findViewById(R.id.btnEntregada)
        val btnCalificar: Button = view.findViewById(R.id.btnCalificar)
        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)
        val btnRechazar: Button = view.findViewById(R.id.btnRechazar)
        val tvCalificacion: TextView = view.findViewById(R.id.tvCalificacion)
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

        val calificacion = data["puntuacion"] as Calificaciones?

        // Asignar datos
        holder.tvProductos.text = donacion.productos
        holder.tvEstadoDonacion.text = "Estado: ${donacion.estado}"
        holder.tvDonante.text = "Donante: $nombreDonante"
        holder.tvTelefonoDonante.text = "Teléfono: $telefonoDonante"
        holder.tvDireccion.text = "Dirección: ${donacion.direccion}"
        holder.tvTipoAlimento.text = "Tipo: $tipoAlimento"
        holder.tvDescripcion.text = donacion.descripcion
        holder.tvFundacionAsignada.text = "Fundación: $nombreFundacion"

        // Imagen
        Glide.with(holder.itemView.context)
            .load(donacion.imagen_url)
            .placeholder(R.drawable.placeholder)
            .into(holder.ivImagenDonacion)

        // Ocultar botones de Aceptar y Rechazar siempre
        holder.btnAceptar.visibility = View.GONE
        holder.btnRechazar.visibility = View.GONE

        // Lógica de visibilidad de Entregada, Calificar y Calificación
        holder.btnEntregada.visibility = View.GONE
        holder.btnCalificar.visibility = View.GONE
        holder.tvCalificacion.visibility = View.GONE

        when (donacion.estado) {
            DonationState.Aceptada.value -> {
                holder.btnEntregada.visibility = View.VISIBLE
            }
            DonationState.Entregada.value -> {
                if (calificacion != null) {
                    holder.tvCalificacion.visibility = View.VISIBLE
                    holder.tvCalificacion.text = "Calificación: ${calificacion.puntuacion}/5"
                } else {
                    holder.btnCalificar.visibility = View.VISIBLE
                }
            }
        }

        // Botón Ubicación
        holder.btnVerUbicacion.setOnClickListener { onVerUbicacionClick(donacion) }

        // Botón Entregada
        holder.btnEntregada.setOnClickListener { onEntregadaClick(donacion) }

        // Botón Calificar
        holder.btnCalificar.setOnClickListener { onCalificarClick(donacion) }
    }

    override fun getItemCount(): Int = donaciones.size
}