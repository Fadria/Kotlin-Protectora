package com.feadca.protectora.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Data class que usaremos para las revisiones de los animales
@Parcelize
data class Revision(
    val volunteerName: String?,
    val date: String,
    val observations: String,
    val idAnimal: Int
) : Parcelable