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

class DonacionUsuarioAdapter(
    private val donaciones: List<Map<String, Any?>>,
    private val onDeleteClick: (Donaciones) -> Unit
) : RecyclerView.Adapter<DonacionUsuarioAdapter.DonacionViewHolder>() {

    inner class DonacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductos: TextView = view.findViewById(R.id.tvProductos)
        val tvEstadoDonacion: TextView = view.findViewById(R.id.tvEstadoDonacion)
        val tvNombreDonante: TextView = view.findViewById(R.id.tvNombreDonante)
        val tvTelefonoDonante: TextView = view.findViewById(R.id.tvTelefonoDonante)
        val tvFundacionAsignada: TextView = view.findViewById(R.id.tvFundacionAsignada)
        val tvDireccion: TextView = view.findViewById(R.id.tvDireccion)
        val tvTipoAlimento: TextView = view.findViewById(R.id.tvTipoAlimento)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val ivImagenDonacion: ImageView = view.findViewById(R.id.ivImagenDonacion)
        val btnEliminar: Button = view.findViewById(R.id.btnEliminarDonacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donacion, parent, false)
        return DonacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonacionViewHolder, position: Int) {
        val data = donaciones[position]
        val donacion = data["donacion"] as Donaciones
        val nombreDonante = data["nombre_donante"] as String
        val telefonoDonante = data["telefono_donante"] as String
        val nombreFundacion = data["nombre_fundacion"] as String
        val tipoAlimento = data["tipo_alimento"] as String

        // Set values to UI components
        holder.tvProductos.text = donacion.productos
        holder.tvEstadoDonacion.text = "Estado: ${donacion.estado}"
        holder.tvNombreDonante.text = "Donante: $nombreDonante"
        holder.tvTelefonoDonante.text = "Teléfono: $telefonoDonante"
        holder.tvFundacionAsignada.text = "Fundación: $nombreFundacion"
        holder.tvDireccion.text = "Dirección: ${donacion.direccion}"
        holder.tvTipoAlimento.text = "Tipo de alimento: $tipoAlimento"
        holder.tvDescripcion.text = donacion.descripcion

        // Cargar imagen
        Glide.with(holder.itemView.context)
            .load(donacion.imagen_url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.ivImagenDonacion)

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(donacion)
        }
    }

    override fun getItemCount(): Int = donaciones.size
}