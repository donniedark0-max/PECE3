package com.example.practica3.services

import com.example.practica3.entity.User
import com.example.practica3.repository.ApiClient
import com.example.practica3.repository.ApiService

class UserService {
    private val apiService = ApiClient.createService(ApiService::class.java)

    suspend fun getUsers(): List<User> {
        val response = apiService.getUsers()
        if (response.isSuccessful) {
            return response.body() ?: emptyList() // Devuelve una lista vacía si no hay datos
        } else {
            throw Exception("Error al obtener usuarios: ${response.errorBody()?.string()}")
        }
    }

    suspend fun addUser(user: User): User {
        val response = apiService.addUser(user)
        if (response.isSuccessful) {
            return response.body()!!
        } else {
            throw Exception("Error al agregar usuario: ${response.errorBody()?.string()}")
        }
    }

    suspend fun updateUser(id: String, user: User) {
        val response = apiService.updateUser(id, user)
        if (!response.isSuccessful) {
            throw Exception("Error al actualizar usuario: ${response.errorBody()?.string()}")
        }
    }

    suspend fun deleteUser(id: String) {
        val response = apiService.deleteUser(id)
        if (!response.isSuccessful) {
            throw Exception("Error al eliminar usuario: ${response.errorBody()?.string()}")
        }
    }
    suspend fun getUserById(id: String): User? {
        val response = apiService.getUserById(id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener usuario: ${response.errorBody()?.string()}")
        }
    }
}