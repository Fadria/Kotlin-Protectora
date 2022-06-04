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
import com.feadca.protectora.databinding.FragmentCreateRevisionBinding
import com.feadca.protectora.databinding.FragmentRevisionsBinding
import com.feadca.protectora.viewmodel.RevisionsViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Fragmento usado para crear una revisión
class CreateRevisionFragment : Fragment(R.layout.fragment_create_revision) {
    // Enlace con las vistas
    private var fragmentBinding: FragmentCreateRevisionBinding? = null

    // Variable que contiene la referencia al ViewModel
    private lateinit var revisionsViewModel: RevisionsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_create_revision, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        revisionsViewModel = ViewModelProvider(this)[RevisionsViewModel::class.java]

        // Actualizamos la vinculación a la vista
        val binding = FragmentCreateRevisionBinding.bind(view)
        fragmentBinding = binding

        // Listener para elegir la fecha
        binding.etDate.setOnClickListener {
            chooseDate()
        }

        // Listener para crear una revisión
        binding.btnCreateRevision.setOnClickListener {
            // Obtenemos el token guardado en el dispositivo o el valor null
            val prefs = this.requireActivity().getSharedPreferences(getString(R.string.shared_file), Context.MODE_PRIVATE)
            val token = prefs.getString("TOKEN", null)

            revisionsViewModel.createRevision(binding.etAnimalId.text.toString(), binding.etRevision.text.toString(),
                token , binding.etDate.text.toString())

            // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
            binding.layout.alpha = 0.5f

            // Impedimos que se puedan usar los inputs mientras realizamos la llamada a la API
            binding.etDate.isEnabled = false;
            binding.etRevision.isEnabled = false;
            binding.etAnimalId.isEnabled = false;

            // Reiniciamos los valores de los campos del formulario
            binding.etDate.text.clear()
            binding.etRevision.text.clear()
            binding.etAnimalId.text.clear()

            requireContext()!!.hideKeyboard() // Ocultamos el teclado
        }

        // Observador ejecutado al obtener la respuesta al request de crear una revisión
        revisionsViewModel.createRevisionLD.observe(viewLifecycleOwner) {
            showSnackbar(it!!)

            // Cambiamos la opacidad de la pantalla para que el usuario sepa que puede continuar usando la app
            binding.layout.alpha = 1.0f

            // Permitimos que se puedan usar los inputs de nuevo
            binding.etDate.isEnabled = true;
            binding.etRevision.isEnabled = true;
            binding.etAnimalId.isEnabled = true;
        }
    }

    // Función usada para elegir la fecha de la revisión
    private fun chooseDate() {
        // Inicializamos el datePicker y lo buildeamos
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccione la fecha de la revisión")
            .build()

        // Mostramos el datePicker
        datePicker.show(parentFragmentManager, "DatePicker")

        // Evento que será ejecutado cuando el usuario confirme la fecha
        datePicker.addOnPositiveButtonClickListener {
            // Formateamos la fecha al formato usado en la base de datos
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

            // Actualizamos el valor de nuestra variable
            val date = dateFormatter.format(Date(it))
            fragmentBinding!!.etDate.setText(date)
        }
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow((requireActivity().getWindow().getCurrentFocus())?.windowToken, 0)
    }
}