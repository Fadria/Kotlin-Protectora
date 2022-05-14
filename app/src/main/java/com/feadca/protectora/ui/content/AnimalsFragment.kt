package com.feadca.protectora.ui.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feadca.protectora.R
import com.feadca.protectora.adapters.AnimalAdapter
import com.feadca.protectora.adapters.BlogEntryAdapter
import com.feadca.protectora.databinding.FragmentAnimalsBinding
import com.feadca.protectora.databinding.FragmentBlogBinding
import com.feadca.protectora.model.Animal
import com.feadca.protectora.ui.MainActivity
import com.feadca.protectora.viewmodel.AnimalsViewModel
import com.feadca.protectora.viewmodel.BlogViewModel

class AnimalsFragment : Fragment(R.layout.fragment_animals) {
    // Enlace con las vistas
    private var fragmentBinding: FragmentAnimalsBinding? = null

    // Adaptador que usaremos en el RecyclerView y la lista de animales
    private lateinit var adapter:AnimalAdapter

    // Variable que contiene la referencia al ViewModel
    private lateinit var animalViewModel: AnimalsViewModel

    // Variable que contendrá nuestra lista de animales
    private val animalList = mutableListOf<Animal>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_animals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        animalViewModel = ViewModelProvider(this)[AnimalsViewModel::class.java]

        // Actualizamos la vinculación a la vista
        val binding = FragmentAnimalsBinding.bind(view)
        fragmentBinding = binding

        // Llamamos a la función que prepara nuestro RecyclerView
        initRecyclerView()

        // Llamamos a la función del viewmodel encargada de obtener los animales
        animalViewModel.getAnimals()

        animalViewModel.animalListLD.observe(viewLifecycleOwner) {
            animalList.clear() // Vaciamos la lista
            animalList.addAll(it) // Añadimos todos los elementos del live data
            adapter?.notifyDataSetChanged() // Notificamos de cambios para cargar de nuevo el RecyclerView
        }
    }

    // Función encargada de inicializar nuestro RecyclerView
    private fun initRecyclerView() {
        adapter = AnimalAdapter(animalList)
        fragmentBinding!!.rvAnimals.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        fragmentBinding!!.rvAnimals.adapter = adapter
    }
}