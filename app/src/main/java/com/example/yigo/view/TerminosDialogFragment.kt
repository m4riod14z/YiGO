import android.app.Dialog
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.example.yigo.R

class TerminosDialogFragment(private val onAceptarClick: (Boolean) -> Unit) : DialogFragment() {

    private lateinit var tvContenidoTerminos: TextView
    private lateinit var btnAceptar: Button
    private lateinit var btnCerrar: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_teminos_condiciones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        tvContenidoTerminos = view.findViewById(R.id.tvContenidoTerminos)
        btnAceptar = view.findViewById(R.id.btnAceptarTerminos)
        btnCerrar = view.findViewById(R.id.btnCerrarDialogo)

        // Habilitar scroll en el TextView
        tvContenidoTerminos.movementMethod = ScrollingMovementMethod()

        // Asignar contenido con formato HTML
        val contenidoHtml = getString(R.string.contenido_terminos)
        tvContenidoTerminos.text = HtmlCompat.fromHtml(contenidoHtml, HtmlCompat.FROM_HTML_MODE_LEGACY)

        // Listeners
        btnAceptar.setOnClickListener {
            onAceptarClick(true)
            dismiss()
        }

        btnCerrar.setOnClickListener {
            onAceptarClick(false)
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }
}