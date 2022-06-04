package com.feadca.protectora.ui.content

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.feadca.protectora.databinding.FragmentAnimalBinding
import com.feadca.protectora.viewmodel.AnimalsViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Fragmento que contendrá información sobre un animal en concreto
class AnimalFragment : Fragment(com.feadca.protectora.R.layout.fragment_animal) {
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
        // Inflamos el layout del fragmento
        return inflater.inflate(com.feadca.protectora.R.layout.fragment_animal, container, false)
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

        // Cuando se pulse el botón se solicitará información
        binding.btnInfo.setOnClickListener {
            // Builder con el que crearemos el diálogo
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)

            // Obtenemos el enlace a la vista
            val viewInflated: View = LayoutInflater.from(context)
                .inflate(
                    com.feadca.protectora.R.layout.dialog_animal_contact,
                    getView() as ViewGroup?,
                    false
                )

            // Variable con el enlace al input del email
            val input =
                viewInflated.findViewById<View>(com.feadca.protectora.R.id.etInfoEmail) as EditText

            // Indicamos el botón de confirmación del diálogo
            builder.setPositiveButton(
                getString(com.feadca.protectora.R.string.solicitar)
            ) { dialog, which ->

                // Si los datos han sido rellenados correctamente llamaremos a la función del viewmode
                // encargada de enviar el email de contacto
                if (input.text.toString() != "") {
                    // Llamamos a la función encargada de solicitar información sobre el animal
                    animalViewModel.requestInfo(
                        input.text.toString(),
                        idAnimal,
                        binding.tvName.text.toString()
                    )
                    dialog.dismiss()
                } else {
                    showSnackbar("Por favor, indique un email")
                }
            }

            // Indicamos el botón de cancelación del diálogo
            builder.setNegativeButton(
                getString(com.feadca.protectora.R.string.cancelar)
            ) { dialog, which ->
                // Cerramos el diálogo
                dialog.cancel()
            }

            // Mostramos el modal
            builder.setView(viewInflated)
            builder.show()
        }

        // Observadores usados para mostrar el resultado de la solicitud de información
        animalViewModel.infoLD.observe(viewLifecycleOwner) {
            showSnackbar(it.toString())
        }
        animalViewModel.errorLD.observe(viewLifecycleOwner) {
            showSnackbar(it.toString())
        }

        // Actualización de los valores de la vista tras recibir los datos del animal
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
                .placeholder(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        com.feadca.protectora.R.drawable.loading
                    )
                ) // Imagen mostrada durante la carga
                .error(
                    AppCompatResources.getDrawable(
                        requireContext(),
                        com.feadca.protectora.R.drawable.logo
                    )
                ) // Imagen mostrada en el caso de no poder cargarla
                .into(binding.iwImage) // Indicamos donde serán colocadas las imágenes en la vista

            // Añadimos el listado de imágenes al slider
            val imageList = ArrayList<SlideModel>()

            // Añadimos las imágenes a mostrar en el array
            it.images!!.forEach {
                imageList.add(SlideModel(it.image, it.date))
            }

            if (imageList.size > 0) {
                // Mostramos el slider con las imágenes del animal
                binding.imageSlider.visibility = View.VISIBLE
                binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)
            }
        }
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(fragmentBinding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}