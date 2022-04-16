package com.feadca.protectora.model

data class User(
    val token: String?,
    val usuario: String,
    val rol: String,
    val foto: String
)