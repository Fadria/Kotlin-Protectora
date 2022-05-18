package com.feadca.protectora.ui.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentAnimalBinding
import com.feadca.protectora.viewmodel.AnimalsViewModel
import com.google.android.material.snackbar.Snackbar

class AnimalFragment : Fragment(R.layout.fragment_animal) {
    // Variable que contendrá la id del animal
    var idAnimal: Int = 0

    // Variable que contiene la referencia al ViewModel
    private lateinit var animalViewModel: AnimalsViewModel

    // Enlace con las vistas
    private var fragmentBinding: FragmentAnimalBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_animal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        animalViewModel = ViewModelProvider(this)[AnimalsViewModel::class.java]

        // Actualizamos la vinculación a la vista
        val binding = FragmentAnimalBinding.bind(view)
        fragmentBinding = binding

        // Actualizamos el valor de la id del animal
        val arguments = arguments
        idAnimal = arguments!!.getInt("idAnimal")

        // Llamamos a la función del viewmodel encargada de cargar el animal
        animalViewModel.loadAnimal(idAnimal)

        // Una vez se reciba la respuesta de la API actualizamos los valores
        animalViewModel.animalDataLD.observe(viewLifecycleOwner) {
            // Ocultamos el texto que marcaba que se estaba cargando el animal
            binding.tvLoading.visibility = View.GONE

            // Mostramos los datos del animal
            binding.tvName.text = it.name
            binding.tvData1.text = it.species + " " + it.gender + " " + it.size
            binding.table.visibility = View.VISIBLE;
            binding.tvAge.text = it.age.toString() + " años"
            binding.tvChip.text = it.chip
            binding.tvUrgent.text = it.urgent
            binding.tvSterilized.text = it.sterilized
            binding.tvExotic.text = it.exotic
            binding.tvHair.text = it.hair
            binding.tvPPP.text = it.dangerousDog
            binding.tvHistory.text = it.history

            // Añadimos el valor de la imagen
            Glide.with(requireContext())
                .load(it.image) // Imagen a mostrar
                .placeholder(AppCompatResources.getDrawable(requireContext(), R.drawable.loading)) // Imagen mostrada durante la carga
                .error(AppCompatResources.getDrawable(requireContext(), R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                .into(binding.iwImage) // Indicamos donde serán colocadas las imágenes en la vista

            // Añadimos el listado de imágenes al slider
            val imageList = ArrayList<SlideModel>()

            // Añadimos las imágenes a mostrar en el array
            it.images!!.forEach {
                imageList.add(SlideModel(it.image, it.date))
            }

            // Mostramos el slider con las imágenes del animal
            binding.imageSlider.visibility = View.VISIBLE
            binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
        }
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}