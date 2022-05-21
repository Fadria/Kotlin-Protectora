package com.feadca.protectora.model

// Data class que usaremos para almacenar los datos del usuario
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
)