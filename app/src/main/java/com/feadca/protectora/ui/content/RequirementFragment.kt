package com.feadca.protectora.ui.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentRequirementBinding
import com.feadca.protectora.model.Requirement
import com.feadca.protectora.viewmodel.RequirementsViewModel

// Fragmento que contendrá los datos de un requisito previo a una adopción
class RequirementFragment : Fragment(R.layout.fragment_requirement) {
    // Variable que contendrá la id del requisito, se aplica el valor 0 para definir la variable
    var requirementId: Int = 0

    // Enlace con las vistas
    private var binding: FragmentRequirementBinding? = null

    // Variable que contiene la referencia al ViewModel
    private lateinit var requirementViewModel: RequirementsViewModel

    // Variable con los datos del requisito
    private lateinit var requirement: Requirement

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_requirement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Actualizamos el valor de la id del requisito
        val arguments = arguments
        requirementId = arguments!!.getInt("idRequirement")

        // Indicamos el fichero que contiene el ViewModel
        requirementViewModel = ViewModelProvider(this)[RequirementsViewModel::class.java]

        // Actualizamos la vinculación a la vista
        binding = FragmentRequirementBinding.bind(view)

        // Llamamos a la función encargada de obtener los datos del requisito
        requirementViewModel.loadRequirement(requirementId)

        // Observamos el LD del requisito para poder mostrar sus datos una vez sea cargado
        requirementViewModel.requirementLD.observe(viewLifecycleOwner) {
            binding!!.tvTitle.text = it!!.title
            binding!!.tvContent.text = it!!.content

            // Cargamos la imagen del requisito
            Glide.with(requireContext())
                .load(it.image) // Imagen a mostrar
                .placeholder(AppCompatResources.getDrawable(requireContext(), R.drawable.loading)) // Imagen mostrada durante la carga
                .error(AppCompatResources.getDrawable(requireContext(), R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                .into(binding!!.iwImage) // Indicamos donde serán colocadas las imágenes en la vista
        }
    }
}