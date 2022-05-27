package com.feadca.protectora.adapters

import android.view.View
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ItemBlogBinding
import com.feadca.protectora.databinding.ItemRequirementBinding
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.model.Requirement

class RequirementViewHolder(view: View):RecyclerView.ViewHolder(view) {
    private val binding = ItemRequirementBinding.bind(view) // Referencia a la vista

    fun bind(requirement: Requirement) {
        // Añadimos los campos al item_requirement
        binding.tvRequirement.text = requirement.title
    }
}