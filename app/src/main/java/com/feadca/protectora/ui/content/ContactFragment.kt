package com.feadca.protectora.ui.content

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentContactBinding
import com.feadca.protectora.viewmodel.MailingViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Fragmento que contendrá el formulario de contacto y el mapa con la localización de la protectora
class ContactFragment : Fragment() {
    // Enlace con las vistas
    private var fragmentContactBinding: FragmentContactBinding? = null

    // Variable que contiene la referencia al ViewModel
    private lateinit var mailingViewModel: MailingViewModel

    // Variable que contendrá el mapa con la localización de la protectora
    private val callback = OnMapReadyCallback { googleMap ->
        // Mostramos la localización de la protectora
        val startLocation = LatLng(39.432236, -0.472373)
        googleMap.addMarker(MarkerOptions().position(startLocation).title("Nuevo Lazo"))
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(15.0f))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(startLocation))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // Actualizamos el valor del binding
        val binding = FragmentContactBinding.bind(view)
        fragmentContactBinding = binding

        // Indicamos el fichero que contiene el ViewModel
        mailingViewModel = ViewModelProvider(this)[MailingViewModel::class.java]

        // Acciones ejecutadas al pulsar el botón de contacto
        binding.btnContact.setOnClickListener {
            // Llamamos a la función contact del viewmodel
            mailingViewModel.contact(binding.etFullName.text.toString(), binding.etEmail.text.toString(),
                binding.etReason.text.toString())

            // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
            binding.layout.alpha = 0.5f

            // Impedimos que se puedan usar los inputs mientras realizamos la llamada a la API
            binding.etEmail.isEnabled = false;
            binding.etFullName.isEnabled = false;
            binding.etReason.isEnabled = false;

            requireContext()!!.hideKeyboard() // Ocultamos el teclado
        }

        // Observador ejecutado al detectar una actualización en el LiveData contactLD
        mailingViewModel.contactLD.observe(viewLifecycleOwner) {
            showSnackbar(it!!)

            // Cambiamos la opacidad de la pantalla para que el usuario sepa que puede continuar usando la app
            binding.layout.alpha = 1.0f

            // Permitimos que se puedan usar los inputs de nuevo
            binding.etEmail.isEnabled = true;
            binding.etFullName.isEnabled = true;
            binding.etReason.isEnabled = true;
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
        Snackbar.make(fragmentContactBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}