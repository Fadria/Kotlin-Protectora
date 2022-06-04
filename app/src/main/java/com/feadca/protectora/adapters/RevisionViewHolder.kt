package com.feadca.protectora.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.feadca.protectora.databinding.ItemRevisionBinding
import com.feadca.protectora.model.Revision

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

class RevisionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemRevisionBinding.bind(view) // Referencia a la vista

    fun bind(revision: Revision) {
        // Añadimos los campos al item_revision
        binding.tvTitle.text = revision.date + " - " + revision.volunteerName
        binding.tvContent.text = revision.observations
    }
}