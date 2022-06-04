package com.feadca.protectora.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feadca.protectora.R
import com.feadca.protectora.model.Animal
import com.feadca.protectora.ui.MainActivity
import com.feadca.protectora.ui.content.ProfileFragment

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

class AnimalAdapter(private val animalList: List<Animal>): RecyclerView.Adapter<AnimalViewHolder>() {

    // Función para inflar nuestro ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return AnimalViewHolder(layoutInflater.inflate(R.layout.item_animal, parent, false))
    }

    // Función para obener el total de elementos
    override fun getItemCount(): Int = animalList.size

    // Función que se ejecutará por cada elemento del ViewHolder
    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        val item = animalList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            (it.context as MainActivity).loadAnimal(item.id)
        }
    }
}