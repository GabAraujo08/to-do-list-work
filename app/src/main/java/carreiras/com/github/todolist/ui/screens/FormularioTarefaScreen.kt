package carreiras.com.github.todolist.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import carreiras.com.github.todolist.ui.theme.FiaptodolistTheme
import carreiras.com.github.todolist.ui.viewmodel.TarefaViewModel

@Composable
fun FormularioTarefaScreen(
    viewModel: TarefaViewModel,
    tarefaId: Int?,
    aoSalvar: () -> Unit,
    aoVoltar: () -> Unit
) {
    // tarefaId == null -> modo cadastro. tarefaId != null -> modo edição:
    // busca a tarefa existente na ViewModel para pré-preencher os campos.
    val tarefaExistente = tarefaId?.let { viewModel.buscarPorId(it) }

    FormularioTarefaConteudo(
        modoEdicao = tarefaExistente != null,
        tituloInicial = tarefaExistente?.titulo ?: "",
        descricaoInicial = tarefaExistente?.descricao ?: "",
        aoVoltar = aoVoltar,
        aoSalvar = { titulo, descricao ->
            if (tarefaExistente != null) {
                viewModel.atualizar(tarefaExistente.copy(titulo = titulo, descricao = descricao))
            } else {
                viewModel.inserir(titulo, descricao)
            }
            aoSalvar()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormularioTarefaConteudo(
    modoEdicao: Boolean,
    tituloInicial: String,
    descricaoInicial: String,
    aoVoltar: () -> Unit,
    aoSalvar: (titulo: String, descricao: String) -> Unit
) {
    var titulo by remember { mutableStateOf(tituloInicial) }
    var descricao by remember { mutableStateOf(descricaoInicial) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(if (modoEdicao) "Editar tarefa" else "Nova tarefa") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            )
            Button(
                onClick = { aoSalvar(titulo, descricao) },
                enabled = titulo.isNotBlank(),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Salvar")
            }
            Button(
                onClick = aoVoltar,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Cancelar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FormularioTarefaPreviewCadastro() {
    FiaptodolistTheme {
        FormularioTarefaConteudo(
            modoEdicao = false,
            tituloInicial = "",
            descricaoInicial = "",
            aoVoltar = {},
            aoSalvar = { _, _ -> }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FormularioTarefaPreviewEdicao() {
    FiaptodolistTheme {
        FormularioTarefaConteudo(
            modoEdicao = true,
            tituloInicial = "Estudar Compose",
            descricaoInicial = "Revisar Navigation",
            aoVoltar = {},
            aoSalvar = { _, _ -> }
        )
    }
}

