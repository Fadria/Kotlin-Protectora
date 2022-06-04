package com.feadca.protectora.adapters

import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ItemAnimalBinding
import com.feadca.protectora.model.Animal

class AnimalViewHolder(view: View):RecyclerView.ViewHolder(view) {
    private val binding = ItemAnimalBinding.bind(view) // Referencia a la vista

    fun bind(animal: Animal) {
        // Añadimos el valor de la imagen
        Glide.with(itemView.context)
            .load(animal.image) // Imagen a mostrar
            .placeholder(getDrawable(itemView.context, R.drawable.loading)) // Imagen mostrada durante la carga
            .error(getDrawable(itemView.context, R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
            .into(binding.ivAnimal) // Indicamos donde serán colocadas las imágenes en la vista

        // Añadimos los campos al item_animal
        binding.tvName.text = animal.name
        binding.tvAge.text = animal.age.toString() + " años"
        binding.tvSize.text = animal.size
        binding.tvSpecies.text = animal.species

        // Según el género del animal se cargará un icono u otro
        when (animal.gender) {
            "macho" -> binding.ivGender.setImageResource(R.drawable.icon_male)
            "hembra" -> binding.ivGender.setImageResource(R.drawable.icon_female)
            else -> {
                binding.ivGender.setImageResource(R.drawable.icon_unknown)
            }
        }
    }
}