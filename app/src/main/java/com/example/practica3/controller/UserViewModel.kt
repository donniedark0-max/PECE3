package com.example.practica3.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica3.services.UserService
import com.example.practica3.entity.User
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val userService = UserService() // Inicialización del servicio
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users // Exposición como StateFlow inmutable

    fun loadUsers() {
        viewModelScope.launch {
            try {
                val userList = userService.getUsers()
                println("Usuarios cargados: $userList") // Agrega este log para depurar
                _users.value = userList // Actualiza el flujo
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            try {
                userService.addUser(user)
                loadUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUser(id: String, user: User) {
        viewModelScope.launch {
            try {
                userService.updateUser(id, user)
                loadUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteUser(id: String) {
        viewModelScope.launch {
            try {
                userService.deleteUser(id)
                loadUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getUserById(id: String): User? {
        return try {
            userService.getUserById(id)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}