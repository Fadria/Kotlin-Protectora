package com.feadca.protectora.ui.content

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentBlogBinding
import com.feadca.protectora.databinding.FragmentRevisionsBinding
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.model.Revision
import com.feadca.protectora.ui.MainActivity
import com.feadca.protectora.viewmodel.BlogViewModel
import com.feadca.protectora.viewmodel.RevisionsViewModel
import com.google.android.material.snackbar.Snackbar

class RevisionsFragment : Fragment(R.layout.fragment_revisions) {
    // Enlace con las vistas
    private var fragmentRevisionsBinding: FragmentRevisionsBinding? = null

    // Variable que contiene la referencia al ViewModel
    private lateinit var revisionsViewModel: RevisionsViewModel

    // Variable que contendrá nuestra lista de revisiones
    private val revisionsList = mutableListOf<Revision>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_revisions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        revisionsViewModel = ViewModelProvider(this)[RevisionsViewModel::class.java]

        // Actualizamos la vinculación a la vista
        val binding = FragmentRevisionsBinding.bind(view)
        fragmentRevisionsBinding = binding

        binding.btnRevisions.setOnClickListener {
            if (binding.etAnimalId.text.toString() != "") {
                // Llamamos a la función del viewmodel encargada de obtener los artículos del blog
                revisionsViewModel.getRevisions(binding.etAnimalId.text.toString().toInt())
            } else {
                showSnackbar("Por favor, indique la ID del animal")
            }
        }

        // Observamos el LiveData de la lista de revisiones
        revisionsViewModel.revisionListLD.observe(viewLifecycleOwner) {
            // Vaciamos el input, cerramos el teclado y navegamos al listado de revisiones
            binding.etAnimalId.text.clear()

            // Llamamos a la función encargada de cargar el fragmento de la lista de revisiones
            (activity as MainActivity?)!!.navigateToRevisionList(it)
        }

        // Observamos el LiveData que controla errores
        revisionsViewModel.errorLD.observe(viewLifecycleOwner) {
            // Mostramos un mensaje de error, vaciamos el input y cerramos el teclado
            showSnackbar(it.toString())
            binding.etAnimalId.text.clear()
            requireContext().hideKeyboard()
        }
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentRevisionsBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(activity?.currentFocus!!.windowToken, 0)
    }
}