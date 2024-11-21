package com.example.practica3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.practica3.controller.UserViewModel
import com.example.practica3.entity.User
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.collectAsState
class FormularioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = UserViewModel()
        val userId = intent.getStringExtra("userId") // Recibimos el ID como String
        println("ID recibido en FormularioActivity: $userId")

        setContent {
            FormScreen(viewModel = viewModel, userId = userId) {
                setResult(RESULT_OK) // Devuelve un resultado para notificar que hay cambios
                finish()
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(viewModel: UserViewModel, userId: String?, onFinish: () -> Unit) {
    val users: List<User> by viewModel.users.collectAsState(initial = emptyList()) // Observar el flujo
    val user = users.find { it.id == userId }
    println("Datos del usuario seleccionado: $user")

    var nombre by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        if (userId != null) {
            try {
                val userFromApi = viewModel.getUserById(userId)
                userFromApi?.let {
                    nombre = it.nombre
                    edad = it.edad.toString()
                    sexo = it.sexo
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(if (userId == null) "Nuevo Usuario" else "Editar Usuario") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre",
                    color = Color.Black
                ) },
                keyboardOptions = KeyboardOptions
                    (keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                )
            )
            OutlinedTextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad",
                    color = Color.Black
                ) },
                keyboardOptions = KeyboardOptions
                    (keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                )
            )
            var expanded by remember { mutableStateOf(false) }
            val listadoSexo = listOf("Femenino", "Masculino")

            // Dropdown field for Sexo
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = sexo,
                    onValueChange = { },
                    label = { Text("Sexo") },
                    modifier = Modifier
                        .menuAnchor() // Asegura que el menú esté correctamente anclado
                        .fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    readOnly = true // Evita que el usuario escriba directamente
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    listadoSexo.forEach { sex ->
                        DropdownMenuItem(
                            text = { Text(text = sex) },
                            onClick = {
                                sexo = sex
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        val user = User(
                            nombre = nombre,
                            edad = edad.toIntOrNull() ?: 0,
                            sexo = sexo
                        )
                        if (userId == null) {
                            println("Agregando usuario: $user")
                            viewModel.addUser(user)
                        } else {
                            println("Actualizando usuario: $userId con datos: $user")
                            viewModel.updateUser(userId, user)
                        }
                        println("Recargando lista de usuarios...")
                        viewModel.loadUsers()
                        onFinish()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (userId == null) "Guardar" else "Actualizar")
            }

            if (userId != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        scope.launch {
                            println("Eliminando usuario con ID: $userId")
                            viewModel.deleteUser(userId)
                            viewModel.loadUsers() // Refrescar usuarios después de eliminar
                            println("Recargando lista de usuarios después de eliminar...")
                            onFinish()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Eliminar")
                }
            }
        }
    }
}