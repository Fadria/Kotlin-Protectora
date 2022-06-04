package com.feadca.protectora.ui.content

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feadca.protectora.R
import com.feadca.protectora.adapters.AnimalAdapter
import com.feadca.protectora.databinding.FragmentAnimalsBinding
import com.feadca.protectora.model.Animal
import com.feadca.protectora.viewmodel.AnimalsViewModel
import com.google.android.material.snackbar.Snackbar

// Fragmento que contendrá el listado de animales de la protectora
class AnimalsFragment : Fragment(R.layout.fragment_animals) {
    // Enlace con las vistas
    private var fragmentBinding: FragmentAnimalsBinding? = null

    // Adaptador que usaremos en el RecyclerView y la lista de animales
    private lateinit var adapter: AnimalAdapter

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
        // Inflamos el layout del fragmento
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

        // Acciones ejecutadas al cargar el listado de animales
        animalViewModel.animalListLD.observe(viewLifecycleOwner) {
            animalList.clear() // Vaciamos la lista
            animalList.addAll(it) // Añadimos todos los elementos del live data
            adapter?.notifyDataSetChanged() // Notificamos de cambios para cargar de nuevo el RecyclerView
        }

        // Observador ejecutado al filtrar animales
        animalViewModel.animalFilteredListLD.observe(viewLifecycleOwner) {
            animalList.clear() // Vaciamos la lista
            animalList.addAll(it) // Añadimos todos los elementos del live data
            adapter?.notifyDataSetChanged() // Notificamos de cambios para cargar de nuevo el RecyclerView
        }

        // Observador usado para mostrar un mensaje de error
        animalViewModel.errorLD.observe(viewLifecycleOwner) {
            showSnackbar(it.toString())
        }

        // Acciones ejecutadas al pulsar el floating button de filtro
        binding.fabFilter.setOnClickListener {
            // Builder con el que crearemos el diálogo
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)

            // Obtenemos el enlace a la vista
            val viewInflated: View = LayoutInflater.from(context)
                .inflate(R.layout.dialog_filter_animal, getView() as ViewGroup?, false)

            // Preparamos los spinners para elegir los valores a filtrar

            // 1. Spinner de las especies
            val speciesSpinner: Spinner = viewInflated.findViewById<Spinner>(R.id.speciesSpinner);
            val species = resources.getStringArray(R.array.species_array)
            val adapterSpecies = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, species
            )
            speciesSpinner.adapter = adapterSpecies

            // 2. Spinner de los géneros
            val genderSpinner: Spinner = viewInflated.findViewById<Spinner>(R.id.genderSpinner);
            val genders = resources.getStringArray(R.array.gender_array)
            val gendersSpecies = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, genders
            )
            genderSpinner.adapter = gendersSpecies


            // 3. Spinner de los tamaños
            val sizesSpinner: Spinner = viewInflated.findViewById<Spinner>(R.id.sizeSpinner);
            val sizes = resources.getStringArray(R.array.size_array)
            val adapterSizes = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_item, sizes
            )
            sizesSpinner.adapter = adapterSizes

            // Indicamos el botón de confirmación del diálogo
            builder.setPositiveButton(
                getString(R.string.filtrar)
            ) { dialog, which ->
                // Filtramos los animales y cerramos el diálogo
                animalViewModel.filterAnimals(
                    speciesSpinner.selectedItem.toString(), // Filtramos los animales
                    sizesSpinner.selectedItem.toString(), genderSpinner.selectedItem.toString()
                )
                dialog.dismiss()
            }

            // Indicamos el botón de cancelación del diálogo
            builder.setNegativeButton(
                getString(R.string.reiniciar)
            ) { dialog, which ->
                // Reiniciamos la lista de los animales y cerramos el diálogo
                animalViewModel.getAnimals()
                dialog.cancel()
            }

            // Mostramos el modal
            builder.setView(viewInflated)
            builder.show()
        }
    }

    // Función encargada de inicializar nuestro RecyclerView
    private fun initRecyclerView() {
        adapter = AnimalAdapter(animalList)
        fragmentBinding!!.rvAnimals.layoutManager =
            LinearLayoutManager(requireActivity().applicationContext)
        fragmentBinding!!.rvAnimals.adapter = adapter
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}