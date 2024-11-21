package com.example.practica3

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.practica3.controller.UserViewModel

// PreviewProvider para MainScreen
class MainScreenPreviewParameterProvider :
    PreviewParameterProvider<Pair<UserViewModel, (String?) -> Unit>> {
    override val values: Sequence<Pair<UserViewModel, (String?) -> Unit>>
        get() = sequenceOf(
            Pair(UserViewModel(), {}) // Instancia mock del ViewModel y lambda vacía
        )
}

// Preview para MainScreen
@Preview(showBackground = true)
@Composable
fun MainScreenPreview(
    @PreviewParameter(MainScreenPreviewParameterProvider::class) params: Pair<UserViewModel, (String?) -> Unit>
) {
    MainScreen(params.first, params.second)
}

class MainActivity : ComponentActivity() {
    private val REQUEST_CODE_FORM = 100 // Código para identificar el formulario
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = UserViewModel()

        setContent {
            MainScreen(viewModel = viewModel) { userId ->
                val intent = Intent(this, FormularioActivity::class.java)
                intent.putExtra("userId", userId)
                startActivityForResult(intent, REQUEST_CODE_FORM)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FORM && resultCode == RESULT_OK) {
            viewModel.loadUsers() // Recarga la lista de usuarios
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: UserViewModel, onNavigateToForm: (String?) -> Unit) {
    val users by viewModel.users.collectAsState()
    println("Usuarios observados en MainScreen: $users")

    LaunchedEffect(Unit) {
        println("Cargando lista de usuarios en MainScreen...")
        viewModel.loadUsers()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = Color(0xFFFFF9FB)
    ) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(
            "Lista de Usuarios",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Color.DarkGray,
            modifier = Modifier
                .padding( horizontal = 45.dp)
            ) }) },

        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(null) },
                content = {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar Usuario"  )
                },
                modifier = Modifier
                    .size(70.dp))
        }
    ) { paddingValues -> // Añadir paddingValues
        Column(
            modifier = Modifier
                .padding(paddingValues) // Aplicar padding del Scaffold
                .fillMaxSize()
        ) {
            // Spacer para crear espacio debajo de la TopAppBar
            Spacer(
                modifier = Modifier
                    .height(16.dp)
            )


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(users, key = { it.id ?: it.hashCode() }) { user ->
                    println("Renderizando usuario: $user") // Verifica que se esté actualizando
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = { onNavigateToForm(user.id) } // Pasar el ID al formulario
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "Nombre: ${user.nombre}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                            Text(
                                "Edad: ${user.edad}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                            Text(
                                "Sexo: ${user.sexo}",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
    }
}