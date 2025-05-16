package com.example.yigo.model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R
import com.example.yigo.controller.AdminUsersController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FundacionAdminAdapter(
    private val fundaciones: List<Map<String, Any?>>,
    private val onEstadoCambiado: () -> Unit
) : RecyclerView.Adapter<FundacionAdminAdapter.FundacionViewHolder>() {

    inner class FundacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreFundacion: TextView = view.findViewById(R.id.tvNombreFundacion)
        val tvNecesidades: TextView = view.findViewById(R.id.tvNecesidades)
        val tvDireccion: TextView = view.findViewById(R.id.tvDireccion)
        val tvRepresentante: TextView = view.findViewById(R.id.tvRepresentante)
        val tvTelefonoRepresentante: TextView = view.findViewById(R.id.tvTelefonoRepresentante)
        val tvEstadoFundacion: TextView = view.findViewById(R.id.tvEstadoFundacion)
        val btnActivar: Button = view.findViewById(R.id.btnActivarFundacion)
        val btnDesactivar: Button = view.findViewById(R.id.btnDesactivarFundacion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FundacionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fundacion_admin, parent, false)
        return FundacionViewHolder(view)
    }

    override fun onBindViewHolder(holder: FundacionViewHolder, position: Int) {
        val data = fundaciones[position]
        val usuario = data["usuario"] as? Usuario
        val fundacion = data["fundacion"] as? Map<String, String>

        if (usuario == null || fundacion == null) {
            Log.e("FundacionAdapter", "Usuario o fundación es null en la posición $position")
            return
        }

        holder.tvNombreFundacion.text = fundacion["nombre_fundacion"] ?: "No disponible"
        holder.tvNecesidades.text = fundacion["necesidades"] ?: "No disponible"
        holder.tvDireccion.text = fundacion["direccion"] ?: "No disponible"
        holder.tvRepresentante.text = fundacion["nombre_representante"] ?: "No disponible"
        holder.tvTelefonoRepresentante.text = fundacion["telefono_representante"] ?: "No disponible"
        holder.tvEstadoFundacion.text = if (usuario.estado == "activo") "Activo" else "Inactivo"
        holder.tvEstadoFundacion.setTextColor(if (usuario.estado == "activo") 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())

        // Ajustar visibilidad de botones según el estado
        if (usuario.estado == "activo") {
            holder.btnActivar.visibility = View.GONE
            holder.btnDesactivar.visibility = View.VISIBLE
        } else {
            holder.btnActivar.visibility = View.VISIBLE
            holder.btnDesactivar.visibility = View.GONE
        }

        // Listener para activar
        holder.btnActivar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val resultado = AdminUsersController.alternarEstadoUsuario(usuario.id)
                if (resultado) onEstadoCambiado()
            }
        }

        // Listener para desactivar
        holder.btnDesactivar.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val resultado = AdminUsersController.alternarEstadoUsuario(usuario.id)
                if (resultado) onEstadoCambiado()
            }
        }
    }

    override fun getItemCount(): Int = fundaciones.size
}