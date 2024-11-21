package com.example.practica3.entity

data class User(
    val id: String? = null, // Opcional: manejado por el servidor
    val nombre: String,
    val edad: Int,
    val sexo: String,
    val imagen: String? = null // Nueva propiedad para almacenar la imagen en Base64

)