package com.feadca.protectora.ui.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentAboutBinding

// Fragmento que contendrá información sobre la protectora
class AboutFragment : Fragment(R.layout.fragment_about) {
    // Enlace con las vistas
    private var binding: FragmentAboutBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Actualizamos la vinculación a la vista
        binding = FragmentAboutBinding.bind(view)
    }

    // Operaciones realizadas al finalizar la vista
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}