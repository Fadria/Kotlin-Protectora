package com.feadca.protectora.model

// Data class que usaremos para almacenar los datos del usuario
data class User(
    val token: String?,
    val usuario: String,
    val rol: String,
    val foto: String
)