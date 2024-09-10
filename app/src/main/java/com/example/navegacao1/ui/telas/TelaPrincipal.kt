package com.example.navegacao1.ui.telas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.navegacao1.model.dados.Endereco
import com.example.navegacao1.model.dados.RetrofitClient
import com.example.navegacao1.model.dados.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun TelaPrincipal(modifier: Modifier = Modifier) {
    var scope = rememberCoroutineScope()
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var idUsuarioBusca by remember { mutableStateOf("") }
    var usuarioEncontrado by remember { mutableStateOf<Usuario?>(null) }
    var nomeUsuario by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = "Tela Principal")

        OutlinedTextField(
            value = nomeUsuario,
            onValueChange = { nomeUsuario = it },
            label = { Text("Nome do Usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            scope.launch {
                try {
                    if (nomeUsuario.isNotBlank()) {
                        val novoUsuario = Usuario(nome = nomeUsuario)
                        RetrofitClient.usuarioService.inserir(novoUsuario)
                        successMessage = "Usuário inserido com sucesso!"
                        usuarios = getUsuarios()
                        nomeUsuario = ""
                    } else {
                        errorMessage = "Nome do usuário não pode estar em branco."
                    }
                } catch (e: Exception) {
                    errorMessage = "Erro ao inserir usuário: ${e.message}"
                }
            }
        }) {
            Text("Inserir Usuário")
        }

        OutlinedTextField(
            value = idUsuarioBusca,
            onValueChange = { idUsuarioBusca = it },
            label = { Text("ID do Usuário para Buscar") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            scope.launch {
                try {
                    if (idUsuarioBusca.isNotBlank()) {
                        usuarioEncontrado = RetrofitClient.usuarioService.buscarPorId(idUsuarioBusca)
                        successMessage = "Usuário encontrado com sucesso!"
                    } else {
                        errorMessage = "ID do usuário não pode estar em branco."
                    }
                } catch (e: Exception) {
                    errorMessage = "Erro ao buscar usuário: ${e.message}"
                }
            }
        }) {
            Text("Buscar Usuário")
        }

        // Mostrar o usuário encontrado
        if (usuarioEncontrado != null) {
            Text(text = "Usuário Encontrado: ${usuarioEncontrado!!.nome}")
        }

        // Mostrar mensagens de erro e sucesso
        if (errorMessage != null) {
            Text(text = errorMessage!!, color = Color.Red)
        }

        if (successMessage != null) {
            Text(text = successMessage!!, color = Color.Green)
        }

        Button(onClick = {
            scope.launch {
                try {
                    usuarios = getUsuarios()
                } catch (e: Exception) {
                    errorMessage = "Erro ao carregar usuários: ${e.message}"
                }
            }
        }) {
            Text("Carregar Usuários")
        }

        LazyColumn {
            items(usuarios) { usuario ->
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Column {
                        Text(text = usuario.nome)
                        Button(onClick = {
                            scope.launch {
                                try {
                                    val id = usuario.id
                                    if (id != null) {
                                        RetrofitClient.usuarioService.remover(id)
                                        usuarios = getUsuarios()
                                        successMessage = "Usuário removido com sucesso!"
                                    }
                                } catch (e: Exception) {
                                    errorMessage = "Erro ao remover usuário: ${e.message}"
                                }
                            }
                        }) {
                            Text("Remover")
                        }
                    }
                }
            }
        }
    }
}

suspend fun getUsuarios(): List<Usuario> {
    return withContext(Dispatchers.IO) {
        RetrofitClient.usuarioService.listar()
    }
}

suspend fun getEndereco(): Endereco {
    return withContext(Dispatchers.IO) {
        RetrofitClient.usuarioService.getEndereco()
    }
}
