package com.feadca.protectora.model

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Data class de los animales de la protectora
data class Animal(
    val id: Int,
    val name: String?,
    val image: String?,
    val chip: String?,
    val species: String?,
    val breed: String?,
    val birth: String?,
    val age: Int?,
    val gender: String?,
    val size: String?,
    val urgent: String?,
    val weight: Double?,
    val sterilized: String?,
    val exotic: String?,
    val observations: String?,
    val hair: String?,
    val history: String?,
    val dangerousDog: String?,
    val images:List<Image>?
)