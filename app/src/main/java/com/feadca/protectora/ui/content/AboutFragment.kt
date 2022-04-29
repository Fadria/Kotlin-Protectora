package com.feadca.protectora.ui.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentAboutBinding

class AboutFragment : Fragment(R.layout.fragment_about) {
    // Enlace con las vistas
    private var fragmentAboutBinding: FragmentAboutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflamos el layout del fragmento about
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Actualizamos la vinculación a la vista
        val binding = FragmentAboutBinding.bind(view)
        fragmentAboutBinding = binding

        // Cuando pulsemos el botón ver animales cambiaremos el fragmento
        binding.btnWatchAnimals.setOnClickListener {
            Toast.makeText(requireContext(), "hola", Toast.LENGTH_SHORT).show()
        }
    }

    // Operaciones realizadas al finalizar la vista
    override fun onDestroyView() {
        fragmentAboutBinding = null
        super.onDestroyView()
    }
}