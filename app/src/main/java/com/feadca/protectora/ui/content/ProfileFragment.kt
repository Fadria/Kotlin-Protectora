package com.feadca.protectora.ui.content

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentProfileBinding
import com.feadca.protectora.model.User
import com.feadca.protectora.ui.MainActivity
import com.feadca.protectora.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Fragmento usado para el perfil del usuario
class ProfileFragment : Fragment() {

    // Variable que contiene la referencia al ViewModel
    private lateinit var viewModel: UserViewModel

    // Variable que contendrá los datos del usuario
    private lateinit var userData: User

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
            userData = it

            fragmentBinding!!.tvName.text = userData.fullName
            fragmentBinding!!.tvRole.text = userData.role

            // Mostramos la imagen correspondiente según el tipo de rol
            when (userData.role) {
                "voluntario" -> {
                    // Añadimos la imagen del rol
                    Glide.with(requireContext())
                        .load(R.drawable.default_user_icon) // Imagen a mostrar
                        .placeholder(AppCompatResources.getDrawable(requireContext(), R.drawable.loading)) // Imagen mostrada durante la carga
                        .error(AppCompatResources.getDrawable(requireContext(), R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                        .into(fragmentBinding!!.imgUser) // Indicamos donde serán colocadas las imágenes en la vista

                }
                "adoptante" -> {
                    // Añadimos la imagen del rol
                    Glide.with(requireContext())
                        .load(R.drawable.default_user_icon3) // Imagen a mostrar
                        .placeholder(AppCompatResources.getDrawable(requireContext(), R.drawable.loading)) // Imagen mostrada durante la carga
                        .error(AppCompatResources.getDrawable(requireContext(), R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                        .into(fragmentBinding!!.imgUser) // Indicamos donde serán colocadas las imágenes en la vista
                }
            }
        }

        // Acciones ejecutadas al detectar una actualización en el LiveData errorLD
        viewModel.errorLD.observe(viewLifecycleOwner) {
            showSnackbar(it!!)
        }

        // Acciones ejecutadas al pulsar el botón de edición
        fragmentBinding!!.btnEdit.setOnClickListener {
            (activity as MainActivity?)!!.navigateToEditUser(userData)
        }
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}