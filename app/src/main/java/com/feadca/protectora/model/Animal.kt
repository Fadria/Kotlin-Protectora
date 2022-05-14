package com.feadca.protectora.model

// Data class que usaremos para los animales de la protectora
data class Animal(
    val id: Int,
    val name: String?,
    val image: String?,
    val chip: Boolean?,
    val species: String?,
    val breed: String?,
    val birth: String?,
    val age: Int?,
    val gender: String?,
    val size: String?,
    val urgent: Boolean?,
    val weight: Double?,
    val sterilized: Boolean?,
    val exotic: Boolean?,
    val observations: String?,
    val hair: Boolean?,
    val history: String?,
    val dangerousDog: Boolean?,
    val images:List<Image>?
)