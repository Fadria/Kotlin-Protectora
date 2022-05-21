package com.feadca.protectora.ui.content

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentProfileBinding
import com.feadca.protectora.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    // Variable que contiene la referencia al ViewModel
    private lateinit var viewModel: UserViewModel

    // Enlace con las vistas
    private var fragmentBinding: FragmentProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Actualizamos la vinculación a la vista
        fragmentBinding = FragmentProfileBinding.bind(view)

        // Obtenemos el token guardado en el dispositivo o el valor null
        val prefs = this.requireActivity().getSharedPreferences(getString(R.string.shared_file), Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", null)

        // Llamada a la función del viewmodel encargada de cargar los datos del usuario
        viewModel.loadUserProfile(token)

        // Acciones ejecutadas al detectar una actualización en el LiveData errorLD
        viewModel.userLD.observe(viewLifecycleOwner) {
            fragmentBinding!!.tvName.text = it.fullName
            fragmentBinding!!.tvRole.text = it.role
        }

        // Acciones ejecutadas al detectar una actualización en el LiveData errorLD
        viewModel.errorLD.observe(viewLifecycleOwner) {
            showSnackbar(it!!)
        }

        fragmentBinding!!.btnEdit.setOnClickListener {
            //(activity as MainActivity?)!!.navigateToEditUser(userData)
        }
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}