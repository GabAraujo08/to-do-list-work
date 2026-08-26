package carreiras.com.github.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import carreiras.com.github.todolist.data.TarefaDatabase
import carreiras.com.github.todolist.navigation.AppNavigation
import carreiras.com.github.todolist.repository.TarefaRepository
import carreiras.com.github.todolist.ui.theme.FiaptodolistTheme
import carreiras.com.github.todolist.ui.viewmodel.TarefaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Monta a cadeia de dependências manualmente: Database -> Repository -> ViewModel.
        val dao = TarefaDatabase.getDatabase(applicationContext).tarefaDao()
        val repository = TarefaRepository(dao)
        val viewModelFactory = TarefaViewModel.Factory(repository)

        setContent {
            FiaptodolistTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val tarefaViewModel: TarefaViewModel = viewModel(factory = viewModelFactory)
                    AppNavigation(viewModel = tarefaViewModel)
                }
            }
        }
    }
}
