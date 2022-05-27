package com.feadca.protectora.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feadca.protectora.R
import com.feadca.protectora.model.Requirement
import com.feadca.protectora.ui.MainActivity

class RequirementAdapter(private val requirements: List<Requirement>): RecyclerView.Adapter<RequirementViewHolder>() {

    // Función para inflar nuestro ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RequirementViewHolder(layoutInflater.inflate(R.layout.item_requirement, parent, false))
    }

    // Función para obener el total de elementos
    override fun getItemCount(): Int = requirements.size

    // Función que se ejecutará por cada elemento del ViewHolder
    override fun onBindViewHolder(holder: RequirementViewHolder, position: Int) {
        val item = requirements[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            (it.context as MainActivity).loadRequirement(item.id)
        }
    }
}