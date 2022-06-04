package com.feadca.protectora.model

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Data class de los requisitos previos a una adopción
data class Requirement(
    val id: Int,
    val title: String?,
    val image: String?,
    val content: String?,
)
