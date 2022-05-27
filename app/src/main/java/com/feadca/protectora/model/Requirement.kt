package com.feadca.protectora.model

// Data class que usaremos para los requisitos previos a una adopción
data class Requirement(
    val id: Int,
    val title: String?,
    val image: String?,
    val content: String?,
)
