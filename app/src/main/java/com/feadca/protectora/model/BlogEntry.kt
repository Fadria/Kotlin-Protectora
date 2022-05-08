package com.feadca.protectora.model

// Data class que usaremos para las entradas del blog
data class BlogEntry(
    val id: Int,
    val title: String?,
    val date: String,
    val image: String,
    val author: String,
    val content: String
)