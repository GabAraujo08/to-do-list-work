package carreiras.com.github.todolist.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import carreiras.com.github.todolist.ui.screens.FormularioTarefaScreen
import carreiras.com.github.todolist.ui.screens.ListaTarefasScreen
import carreiras.com.github.todolist.ui.viewmodel.TarefaViewModel

/** Nomes de rota centralizados para evitar strings soltas pelo código. */
object Rotas {
    const val LISTA = "lista"
    const val FORMULARIO = "formulario"
    const val ARG_TAREFA_ID = "tarefaId"

    /** Rota do formulário sem tarefa selecionada = modo cadastro. */
    fun formularioNovaTarefa() = "$FORMULARIO?$ARG_TAREFA_ID=-1"

    /** Rota do formulário com id preenchido = modo edição. */
    fun formularioEditarTarefa(id: Int) = "$FORMULARIO?$ARG_TAREFA_ID=$id"
}

@Composable
fun AppNavigation(viewModel: TarefaViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Rotas.LISTA) {
        composable(Rotas.LISTA) {
            ListaTarefasScreen(
                viewModel = viewModel,
                aoClicarNovaTarefa = { navController.navigate(Rotas.formularioNovaTarefa()) },
                aoClicarTarefa = { id -> navController.navigate(Rotas.formularioEditarTarefa(id)) }
            )
        }
        composable(
            route = "${Rotas.FORMULARIO}?${Rotas.ARG_TAREFA_ID}={${Rotas.ARG_TAREFA_ID}}",
            arguments = listOf(
                navArgument(Rotas.ARG_TAREFA_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val idArgumento = backStackEntry.arguments?.getInt(Rotas.ARG_TAREFA_ID) ?: -1
            FormularioTarefaScreen(
                viewModel = viewModel,
                tarefaId = idArgumento.takeIf { it != -1 },
                aoSalvar = { navController.popBackStack() },
                aoVoltar = { navController.popBackStack() }
            )
        }
    }
}
