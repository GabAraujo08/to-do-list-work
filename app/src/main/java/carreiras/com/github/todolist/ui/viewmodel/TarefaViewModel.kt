package carreiras.com.github.todolist.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import carreiras.com.github.todolist.data.Tarefa
import carreiras.com.github.todolist.repository.TarefaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel responsável por expor o estado da lista de tarefas para a UI
 * e por encaminhar as ações do usuário (inserir, atualizar, excluir) para
 * o Repository, sempre em uma coroutine dentro do escopo da própria ViewModel.
 */
class TarefaViewModel(private val repository: TarefaRepository) : ViewModel() {

    val tarefas: StateFlow<List<Tarefa>> = repository.todasAsTarefas.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun inserir(titulo: String, descricao: String) {
        viewModelScope.launch {
            repository.inserir(Tarefa(titulo = titulo, descricao = descricao))
        }
    }

    fun atualizar(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.atualizar(tarefa)
        }
    }

    fun alternarConclusao(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.atualizar(tarefa.copy(concluida = !tarefa.concluida))
        }
    }

    fun deletar(tarefa: Tarefa) {
        viewModelScope.launch {
            repository.deletar(tarefa)
        }
    }

    fun buscarPorId(id: Int): Tarefa? = tarefas.value.find { it.id == id }

    /**
     * Factory necessária pois a ViewModel possui um construtor com dependência
     * (TarefaRepository), que o ViewModelProvider padrão não sabe instanciar.
     */
    class Factory(private val repository: TarefaRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TarefaViewModel::class.java)) {
                return TarefaViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
        }
    }
}
