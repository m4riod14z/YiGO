package com.example.yigo.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R

class FoundationAdapter(
    private val fundaciones: List<Fundacion>,
    private val onSelectClick: (Fundacion) -> Unit,
    private val onLocationClick: (Fundacion) -> Unit
) : RecyclerView.Adapter<FoundationAdapter.FoundationViewHolder>() {

    inner class FoundationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNombreFundacion: TextView = itemView.findViewById(R.id.tvNombreFundacion)
        val tvNecesidades: TextView = itemView.findViewById(R.id.tvNecesidades)
        val tvDireccion: TextView = itemView.findViewById(R.id.tvDireccion)
        val tvRepresentante: TextView = itemView.findViewById(R.id.tvRepresentante)
        val tvTelefonoRepresentante: TextView = itemView.findViewById(R.id.tvTelefonoRepresentante)
        val btnSeleccionar: Button = itemView.findViewById(R.id.btnSeleccionar)
        val btnUbicacion: Button = itemView.findViewById(R.id.btnUbicacion)

        fun bind(fundacion: Fundacion) {
            tvNombreFundacion.text = fundacion.nombre_fundacion
            tvNecesidades.text = fundacion.necesidades
            tvDireccion.text = fundacion.direccion
            tvRepresentante.text = fundacion.nombre_representante
            tvTelefonoRepresentante.text = fundacion.telefono_representante

            btnSeleccionar.setOnClickListener {
                onSelectClick(fundacion)
            }

            btnUbicacion.setOnClickListener {
                onLocationClick(fundacion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoundationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fundacion, parent, false)
        return FoundationViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoundationViewHolder, position: Int) {
        holder.bind(fundaciones[position])
    }

    override fun getItemCount(): Int = fundaciones.size
}