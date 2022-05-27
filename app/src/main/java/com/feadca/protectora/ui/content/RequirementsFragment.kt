package com.feadca.protectora.ui.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feadca.protectora.R
import com.feadca.protectora.adapters.BlogEntryAdapter
import com.feadca.protectora.adapters.RequirementAdapter
import com.feadca.protectora.databinding.FragmentRequirementsBinding
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.model.Requirement
import com.feadca.protectora.viewmodel.BlogViewModel
import com.feadca.protectora.viewmodel.RequirementsViewModel

class RequirementsFragment : Fragment(R.layout.fragment_requirements) {
    // Enlace con las vistas
    private var binding: FragmentRequirementsBinding? = null

    // Adaptador que usaremos en el RecyclerView de los requisitos
    private lateinit var adapter: RequirementAdapter

    // Variable que contiene la referencia al ViewModel
    private lateinit var requirementViewModel: RequirementsViewModel

    // Variable que contendrá nuestra lista de requisitos
    private val requirementList = mutableListOf<Requirement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_requirements, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        requirementViewModel = ViewModelProvider(this)[RequirementsViewModel::class.java]

        // Actualizamos la vinculación a la vista
        binding = FragmentRequirementsBinding.bind(view)

        // Llamamos a la función que prepara nuestro RecyclerView
        initRecyclerView()

        // Llamamos a la función del viewmodel encargada de obtener los requisitos
        requirementViewModel.getRequirements()

        // Observer usado para actualizar los requisitos
        requirementViewModel.requirementsLD.observe(viewLifecycleOwner) {
            requirementList.clear() // Vaciamos la lista
            requirementList.addAll(it) // Añadimos todos los elementos del live data
            adapter?.notifyDataSetChanged() // Notificamos de cambios para cargar de nuevo el RecyclerView
        }
    }

    // Función encargada de inicializar nuestro RecyclerView
    private fun initRecyclerView() {
        adapter = RequirementAdapter(requirementList)
        binding!!.rvRequirements.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        binding!!.rvRequirements.adapter = adapter
    }
}