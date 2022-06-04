package com.feadca.protectora.ui.content

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentBecomeVolunteerBinding
import com.feadca.protectora.databinding.FragmentContactBinding
import com.feadca.protectora.viewmodel.MailingViewModel
import com.google.android.material.snackbar.Snackbar

// Fragmento que contendrá el formulario para convertirse en voluntario
class BecomeVolunteerFragment : Fragment() {
    // Enlace con las vistas
    private var binding: FragmentBecomeVolunteerBinding? = null

    // Variable que contiene la referencia al ViewModel
    private lateinit var mailingViewModel: MailingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_become_volunteer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Actualizamos el valor del binding
        binding = FragmentBecomeVolunteerBinding.bind(view)

        // Indicamos el fichero que contiene el ViewModel
        mailingViewModel = ViewModelProvider(this)[MailingViewModel::class.java]

        // Listener usado al pulsar el botón para enviar el email
        binding!!.btnSendMail.setOnClickListener {
            // Llamamos a la función encargada de enviar el mail del voluntario
            mailingViewModel.sendVolunteerMail(binding!!.etEmail.text.toString())

            // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
            binding!!.layout.alpha = 0.5f

            // Impedimos que se puedan usar los inputs mientras realizamos la llamada a la API
            binding!!.etEmail.isEnabled = false;

            requireContext()!!.hideKeyboard() // Ocultamos el teclado
        }

        // Acciones ejecutadas al detectar una actualización en el LiveData
        mailingViewModel.volunteerLD.observe(viewLifecycleOwner) {
            showSnackbar(it!!)

            // Cambiamos la opacidad de la pantalla para que el usuario sepa que puede continuar usando la app
            binding!!.layout.alpha = 1.0f

            // Permitimos que se puedan usar los inputs de nuevo
            binding!!.etEmail.isEnabled = true;
        }
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow((requireActivity().getWindow().getCurrentFocus())?.windowToken, 0)
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(binding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}