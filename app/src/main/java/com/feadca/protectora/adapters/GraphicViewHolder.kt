package com.feadca.protectora.adapters

import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ItemGraphicBinding
import com.feadca.protectora.model.Graphic

class GraphicViewHolder(view: View): RecyclerView.ViewHolder(view) {
    private val binding = ItemGraphicBinding.bind(view) // Referencia a la vista

    fun bind(graphic: Graphic) {
        // Añadimos el valor de la imagen
        Glide.with(itemView.context)
            .load(graphic.image) // Imagen a mostrar
            .placeholder(AppCompatResources.getDrawable(itemView.context, R.drawable.loading)) // Imagen mostrada durante la carga
            .error(AppCompatResources.getDrawable(itemView.context, R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
            .into(binding.iwImage) // Indicamos donde serán colocadas las imágenes en la vista

        // Añadimos los campos al item_graphic
        binding.tvTitle.text = graphic.title
        binding.tvDate.text = graphic.date
    }
}