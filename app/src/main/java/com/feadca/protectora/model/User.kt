package com.feadca.protectora.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Data class de los usuarios de la protectora
// Es una clase parcelable para poder enviar sus objetos entre fragmentos
@Parcelize
data class User(
    val token: String?,
    val user: String?,
    val role: String?,
    val image: String?,
    val fullName: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val city: String?,
    val zipCode: String?,
    val licensePPP: String?,
    val birthDay: String?
) : Parcelable