package com.feadca.protectora.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feadca.protectora.R
import com.feadca.protectora.model.Graphic
import com.feadca.protectora.ui.MainActivity

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

class GraphicAdapter(private val graphicList: List<Graphic>): RecyclerView.Adapter<GraphicViewHolder>() {

    // Función para inflar nuestro ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GraphicViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GraphicViewHolder(layoutInflater.inflate(R.layout.item_graphic, parent, false))
    }

    // Función para obener el total de elementos
    override fun getItemCount(): Int = graphicList.size

    // Función que se ejecutará por cada elemento del ViewHolder
    override fun onBindViewHolder(holder: GraphicViewHolder, position: Int) {
        val item = graphicList[position]
        holder.bind(item)
    }
}