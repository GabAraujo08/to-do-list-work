package carreiras.com.github.todolist.repository

import carreiras.com.github.todolist.data.Tarefa
import carreiras.com.github.todolist.data.TarefaDao
import kotlinx.coroutines.flow.Flow

/**
 * Camada de abstração entre a fonte de dados (Room) e o restante da aplicação.
 * A ViewModel não conhece o DAO diretamente: ela conversa apenas com o
 * Repository, o que facilita testes e uma eventual troca de fonte de dados.
 */
class TarefaRepository(private val tarefaDao: TarefaDao) {

    val todasAsTarefas: Flow<List<Tarefa>> = tarefaDao.listarTodas()

    suspend fun inserir(tarefa: Tarefa) {
        tarefaDao.inserir(tarefa)
    }

    suspend fun atualizar(tarefa: Tarefa) {
        tarefaDao.atualizar(tarefa)
    }

    suspend fun deletar(tarefa: Tarefa) {
        tarefaDao.deletar(tarefa)
    }
}
