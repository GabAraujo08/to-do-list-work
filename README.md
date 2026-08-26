# To-Do List — Android (Jetpack Compose)

Aplicativo Android simples de lista de tarefas, desenvolvido como atividade
individual da FIAP. Permite listar, criar, editar, concluir e excluir
tarefas, com persistência local em banco de dados.

## Tecnologias utilizadas

- **Kotlin**
- **Jetpack Compose** — construção de toda a interface declarativa
- **Room** — persistência local em SQLite
- **Coroutines / Flow** — operações assíncronas e observação reativa dos dados
- **ViewModel** — retenção de estado através de mudanças de configuração
- **Navigation Compose** — navegação entre as telas do app

## Arquitetura

O projeto segue o padrão **MVVM** com uma camada de Repository:

```
UI (Compose) -> ViewModel -> Repository -> DAO (Room) -> Banco de dados
```

### TarefaRepository

Fica entre a `TarefaViewModel` e o `TarefaDao`. Sua responsabilidade é
isolar a origem dos dados: a ViewModel não sabe que existe Room por trás,
apenas chama métodos como `inserir`, `atualizar`, `deletar` e observa o
`Flow<List<Tarefa>>` exposto em `todasAsTarefas`. Isso deixa a ViewModel
mais simples de testar e permitiria trocar a fonte de dados no futuro sem
alterar a camada de apresentação.

### TarefaViewModel

Expõe a lista de tarefas como um `StateFlow<List<Tarefa>>` (`tarefas`),
obtido a partir do `Flow` do Repository com `stateIn`, o que mantém o
último estado em cache e o compartilha entre observadores. Todas as
operações de escrita (`inserir`, `atualizar`, `alternarConclusao`,
`deletar`) disparam uma coroutine em `viewModelScope`, garantindo que o
trabalho seja cancelado automaticamente se a ViewModel for destruída.
Como o construtor recebe um `TarefaRepository`, uma `Factory` própria
(`TarefaViewModel.Factory`) é usada para que o `ViewModelProvider`
consiga instanciá-la com essa dependência.

### ListaTarefasScreen

Observa `viewModel.tarefas` com `collectAsState()`, de forma que qualquer
mudança no banco (inserção, edição, exclusão) recalcula a tela
automaticamente. As tarefas são exibidas em uma `LazyColumn`; cada item
tem um `Checkbox` para concluir/desmarcar, um botão para editar (que
dispara a navegação passando o `id` da tarefa) e um botão para excluir.
Um FAB (`FloatingActionButton`) inicia o cadastro de uma nova tarefa. O
conteúdo visual foi extraído para um Composable privado sem ViewModel
(`ListaTarefasConteudo`) para permitir `@Preview`s com dados estáticos,
tanto com itens quanto com a lista vazia.

### FormularioTarefaScreen

Recebe um `tarefaId: Int?` opcional. Quando `null`, a tela está em modo de
**cadastro**; quando diferente de `null`, busca a tarefa correspondente na
ViewModel (`buscarPorId`) e usa seus valores para pré-preencher os campos
de título e descrição — modo de **edição**. Ao salvar, decide entre
`viewModel.inserir(...)` ou `viewModel.atualizar(...)` dependendo do modo,
e então aciona o callback `aoSalvar`, que a navegação usa para voltar à
lista. O botão "Cancelar" aciona `aoVoltar` sem persistir nada. Assim como
na tela de lista, o conteúdo visual foi extraído em um Composable privado
para permitir Previews de cadastro e de edição.

### AppNavigation

Define duas rotas com `NavHost`:

- `lista` — tela inicial, mostra `ListaTarefasScreen`.
- `formulario?tarefaId={tarefaId}` — mostra `FormularioTarefaScreen`. O
  argumento `tarefaId` é opcional (`NavType.IntType`, `defaultValue = -1`).
  Quando a navegação parte do FAB (nova tarefa), nenhum id válido é
  passado (`-1`); quando parte de um item da lista, o `id` real da tarefa
  é passado na rota. Esse valor é convertido para `Int?` (`null` quando
  igual a `-1`) antes de chegar em `FormularioTarefaScreen`, o que é o
  sinal que a tela usa para decidir entre cadastro e edição.

### MainActivity

Monta manualmente a cadeia de dependências: obtém o `TarefaDao` a partir
de `TarefaDatabase.getDatabase(...)`, cria o `TarefaRepository` e, com
ele, a `TarefaViewModel.Factory`. Essa factory é passada para
`viewModel(factory = ...)` dentro do `setContent`, garantindo que a
`TarefaViewModel` sobreviva a recomposições e mudanças de configuração.
Por fim, chama `AppNavigation(viewModel = tarefaViewModel)`, que passa a
ser o conteúdo principal do app — o template de exemplo do Android Studio
("Hello Android") foi removido.

## Como executar

1. Abra o projeto no Android Studio (Ladybug ou superior).
2. Aguarde a sincronização do Gradle.
3. Execute em um emulador ou dispositivo físico com Android 7.0 (API 24) ou superior.

## Evidências

As evidências da execução (telas de listagem, cadastro, edição, conclusão,
exclusão e navegação) estão na pasta [`docs/evidencias`](docs/evidencias).
