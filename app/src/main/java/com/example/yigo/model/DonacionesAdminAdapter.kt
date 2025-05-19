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
import com.example.yigo.controller.DonationProcessController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DonacionesAdminAdapter(
    private val donaciones: List<Map<String, Any?>>,
    private val onEliminarClick: (String, String?) -> Unit
) : RecyclerView.Adapter<DonacionesAdminAdapter.DonacionViewHolder>() {

    inner class DonacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivImagenDonacion: ImageView = view.findViewById(R.id.ivImagenDonacion)
        val tvProductos: TextView = view.findViewById(R.id.tvProductos)
        val tvEstadoDonacion: TextView = view.findViewById(R.id.tvEstadoDonacion)
        val tvDonante: TextView = view.findViewById(R.id.tvDonante)
        val tvTelefonoDonante: TextView = view.findViewById(R.id.tvTelefonoDonante)
        val tvFundacionAsignada: TextView = view.findViewById(R.id.tvFundacionAsignada)
        val tvDireccion: TextView = view.findViewById(R.id.tvDireccion)
        val tvTipoAlimento: TextView = view.findViewById(R.id.tvTipoAlimento)
        val tvDescripcion: TextView = view.findViewById(R.id.tvDescripcion)
        val btnEliminarDonacion: Button = view.findViewById(R.id.btnEliminarDonacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donacion_admin, parent, false)
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

        // Acciones del botón de eliminación
        holder.btnEliminarDonacion.setOnClickListener {
            onEliminarClick(donacion.id, donacion.imagen_url)
        }
    }

    override fun getItemCount(): Int = donaciones.size
}