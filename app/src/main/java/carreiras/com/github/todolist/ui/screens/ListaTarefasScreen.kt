package carreiras.com.github.todolist.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import carreiras.com.github.todolist.data.Tarefa
import carreiras.com.github.todolist.ui.theme.FiaptodolistTheme
import carreiras.com.github.todolist.ui.viewmodel.TarefaViewModel

@Composable
fun ListaTarefasScreen(
    viewModel: TarefaViewModel,
    aoClicarNovaTarefa: () -> Unit,
    aoClicarTarefa: (Int) -> Unit
) {
    val tarefas by viewModel.tarefas.collectAsState()

    ListaTarefasConteudo(
        tarefas = tarefas,
        aoClicarNovaTarefa = aoClicarNovaTarefa,
        aoClicarTarefa = aoClicarTarefa,
        aoAlternarConclusao = { viewModel.alternarConclusao(it) },
        aoExcluir = { viewModel.deletar(it) }
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListaTarefasConteudo(
    tarefas: List<Tarefa>,
    aoClicarNovaTarefa: () -> Unit,
    aoClicarTarefa: (Int) -> Unit,
    aoAlternarConclusao: (Tarefa) -> Unit,
    aoExcluir: (Tarefa) -> Unit
) {
    var tarefaParaExcluir by remember { mutableStateOf<Tarefa?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Minhas tarefas") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = aoClicarNovaTarefa) {
                Icon(Icons.Filled.Add, contentDescription = "Nova tarefa")
            }
        }
    ) { innerPadding ->
        if (tarefas.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp)
            ) {
                Text("Nenhuma tarefa cadastrada. Toque em + para começar.")
            }
        } else {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(tarefas, key = { it.id }) { tarefa ->
                    ItemTarefa(
                        tarefa = tarefa,
                        onClick = { aoClicarTarefa(tarefa.id) },
                        onAlternarConclusao = { aoAlternarConclusao(tarefa) },
                        onExcluir = { tarefaParaExcluir = tarefa }
                    )
                }
            }
        }
    }

    tarefaParaExcluir?.let { tarefa ->
        DialogoConfirmarExclusao(
            tarefa = tarefa,
            onConfirmar = {
                aoExcluir(tarefa)
                tarefaParaExcluir = null
            },
            onCancelar = { tarefaParaExcluir = null }
        )
    }
}

@Composable
private fun DialogoConfirmarExclusao(
    tarefa: Tarefa,
    onConfirmar: () -> Unit,
    onCancelar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Excluir tarefa") },
        text = { Text("Tem certeza que deseja excluir \"${tarefa.titulo}\"? Essa ação não pode ser desfeita.") },
        confirmButton = {
            TextButton(onClick = onConfirmar) {
                Text("Excluir")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun ItemTarefa(
    tarefa: Tarefa,
    onClick: () -> Unit,
    onAlternarConclusao: () -> Unit,
    onExcluir: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Checkbox(checked = tarefa.concluida, onCheckedChange = { onAlternarConclusao() })
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                ) {
                    Text(
                        text = tarefa.titulo,
                        textDecoration = if (tarefa.concluida) TextDecoration.LineThrough else null
                    )
                    if (tarefa.descricao.isNotBlank()) {
                        Text(text = tarefa.descricao)
                    }
                }
            }
            IconButton(onClick = onClick) {
                Text("Editar")
            }
            IconButton(onClick = onExcluir) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir tarefa")
            }
        }
    }
}

@Preview(showBackground = true)

@Composable
private fun ListaTarefasPreviewComItens() {
    FiaptodolistTheme {
        ListaTarefasConteudo(
            tarefas = listOf(
                Tarefa(id = 1, titulo = "Estudar Compose", descricao = "Revisar LazyColumn", concluida = false),
                Tarefa(id = 2, titulo = "Enviar atividade", descricao = "FIAP", concluida = true)
            ),
            aoClicarNovaTarefa = {},
            aoClicarTarefa = {},
            aoAlternarConclusao = {},
            aoExcluir = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ListaTarefasPreviewVazia() {
    FiaptodolistTheme {
        ListaTarefasConteudo(
            tarefas = emptyList(),
            aoClicarNovaTarefa = {},
            aoClicarTarefa = {},
            aoAlternarConclusao = {},
            aoExcluir = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogoConfirmarExclusaoPreview() {
    FiaptodolistTheme {
        DialogoConfirmarExclusao(
            tarefa = Tarefa(id = 1, titulo = "Estudar Compose", descricao = "Revisar LazyColumn"),
            onConfirmar = {},
            onCancelar = {}
        )
    }
}
