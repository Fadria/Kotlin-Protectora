package com.feadca.protectora.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feadca.protectora.R
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.ui.MainActivity
import com.feadca.protectora.ui.content.ProfileFragment

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

class BlogEntryAdapter(private val blogEntries: List<BlogEntry>): RecyclerView.Adapter<BlogEntryViewHolder>() {

    // Función para inflar nuestro ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogEntryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return BlogEntryViewHolder(layoutInflater.inflate(R.layout.item_blog, parent, false))
    }

    // Función para obener el total de elementos
    override fun getItemCount(): Int = blogEntries.size

    // Función que se ejecutará por cada elemento del ViewHolder
    override fun onBindViewHolder(holder: BlogEntryViewHolder, position: Int) {
        val item = blogEntries[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            (it.context as MainActivity).loadArticle(item.id)
        }
    }
}