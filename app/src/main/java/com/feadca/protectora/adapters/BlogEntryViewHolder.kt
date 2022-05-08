package com.feadca.protectora.adapters

import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ItemBlogBinding
import com.feadca.protectora.model.BlogEntry

class BlogEntryViewHolder(view: View):RecyclerView.ViewHolder(view) {
    private val binding = ItemBlogBinding.bind(view) // Referencia a la vista

    fun bind(blogEntry: BlogEntry) {
        // Añadimos el valor de la imagen
        Glide.with(itemView.context)
            .load(blogEntry.image) // Imagen a mostrar
            .placeholder(getDrawable(itemView.context, R.drawable.loading)) // Imagen mostrada durante la carga
            .error(getDrawable(itemView.context, R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
            .into(binding.imgArticle) // Indicamos donde serán colocadas las imágenes en la vista

        // Añadimos los campos al item_blog
        binding.tvContent.text = blogEntry.content
        binding.tvDate.text = blogEntry.date
        binding.tvTitle.text = blogEntry.title
    }
}