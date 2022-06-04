package com.feadca.protectora.model

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

// Data class de las entradas del blog
data class BlogEntry(
    val id: Int,
    val title: String?,
    val date: String,
    val image: String,
    val author: String,
    val content: String,
    val imageFoot: String?
)