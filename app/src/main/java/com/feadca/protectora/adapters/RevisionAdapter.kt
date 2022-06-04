package com.feadca.protectora.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feadca.protectora.R
import com.feadca.protectora.model.Revision

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

class RevisionAdapter(private val revisionList: List<Revision>): RecyclerView.Adapter<RevisionViewHolder>() {
    // Función para inflar nuestro ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RevisionViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RevisionViewHolder(layoutInflater.inflate(R.layout.item_revision, parent, false))
    }

    // Función para obener el total de elementos
    override fun getItemCount(): Int = revisionList.size

    // Función que se ejecutará por cada elemento del ViewHolder
    override fun onBindViewHolder(holder: RevisionViewHolder, position: Int) {
        val item = revisionList[position]
        holder.bind(item)
    }
}