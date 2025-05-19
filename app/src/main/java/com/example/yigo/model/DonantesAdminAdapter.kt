package com.example.yigo.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.yigo.R

class DonantesAdminAdapter(
    private val donantes: List<Map<String, Any?>>,
    private val onEstadoClick: (Usuario) -> Unit,
    private val onEditarClick: (Usuario) -> Unit
) : RecyclerView.Adapter<DonantesAdminAdapter.DonanteViewHolder>() {

    inner class DonanteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombreUsuario: TextView = view.findViewById(R.id.tvNombreUsuario)
        val tvCorreoUsuario: TextView = view.findViewById(R.id.tvCorreoUsuario)
        val tvTelefonoUsuario: TextView = view.findViewById(R.id.tvTelefonoUsuario)
        val tvEstadoUsuario: TextView = view.findViewById(R.id.tvEstadoUsuario)
        val btnEditarUsuario: ImageButton = view.findViewById(R.id.btnEditarUsuario)
        val btnActivar: Button = view.findViewById(R.id.btnActivarUsuario)
        val btnInactivar: Button = view.findViewById(R.id.btnInactivarUsuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonanteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_donante_admin, parent, false)
        return DonanteViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonanteViewHolder, position: Int) {
        val data = donantes[position]
        val usuario = data["usuario"] as Usuario
        val nombre = data["nombre"] as String

        holder.tvNombreUsuario.text = nombre
        holder.tvCorreoUsuario.text = usuario.correo
        holder.tvTelefonoUsuario.text = usuario.telefono
        holder.tvEstadoUsuario.text = if (usuario.estado == "activo") "Activo" else "Inactivo"
        holder.tvEstadoUsuario.setTextColor(if (usuario.estado == "activo") 0xFF4CAF50.toInt() else 0xFFFF5252.toInt())

        // Lógica de visualización de los botones
        if (usuario.estado == "activo") {
            holder.btnActivar.visibility = View.GONE
            holder.btnInactivar.visibility = View.VISIBLE
        } else {
            holder.btnActivar.visibility = View.VISIBLE
            holder.btnInactivar.visibility = View.GONE
        }

        // Acción del botón Activar
        holder.btnActivar.setOnClickListener {
            onEstadoClick(usuario)
        }

        // Acción del botón Inactivar
        holder.btnInactivar.setOnClickListener {
            onEstadoClick(usuario)
        }

        // Acción del botón Editar
        holder.btnEditarUsuario.setOnClickListener {
            onEditarClick(usuario)
        }
    }

    override fun getItemCount(): Int = donantes.size
}