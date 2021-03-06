package com.feadca.protectora.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Data class de las revisiones de los animales
// Es una clase parcelable para poder enviar sus objetos entre fragmentos
@Parcelize
data class Revision(
    val volunteerName: String?,
    val date: String,
    val observations: String,
    val idAnimal: Int
) : Parcelable