package com.example.todonotioncompose.ui.search

import androidx.compose.foundation.background
import com.example.todonotioncompose.R
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.Keyword.Keyword
import com.example.todonotioncompose.model.Todo
import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.theme.TodoNotionComposeTheme
import com.example.todonotioncompose.ui.todo.ErrorScreen
import com.example.todonotioncompose.ui.todo.LoadingScreen
import com.example.todonotioncompose.ui.todo.PhotosGridScreen
import com.example.todonotioncompose.ui.todo.TodoPhotoCard
import com.example.todonotioncompose.ui.todo.TodoUiState
import com.example.todonotioncompose.ui.todo.TodoViewModel
import kotlinx.coroutines.launch

object SearchResultScreenDestination : NavigationDestination {
    override val route = "search_todo_result"
    override val titleRes = R.string.search_btn
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultScreen(
    navigateToTodoDetails: (String) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    searchViewModel: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory),
    todoUiState: TodoUiState, retryAction: () -> Unit,
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    //val uiState by viewModel.todoUiState

    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(SearchScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.LightGray)
        ) {
            KeywordResultEntryBody(
                keywordUiState = searchViewModel.keywordUiState,
                onKeywordValueChange = searchViewModel::updateUiState,
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            )
            Spacer (modifier = Modifier.padding(top = 1.dp))

            when (todoUiState) {
                is TodoUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is TodoUiState.Success -> TodoList(
                    todos = todoUiState.todos, modifier = modifier.fillMaxWidth(),
                    onTodoClick = navigateToTodoDetails, viewModel = viewModel
                )

                is TodoUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
            }

        }
    }
}

@Composable
fun KeywordResultEntryBody(
    keywordUiState: KeywordUiState,
    onKeywordValueChange: (KeywordDetails) -> Unit,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = keywordUiState.keywordDetails.keyName,
            onValueChange = { onKeywordValueChange(keywordUiState.keywordDetails.copy(keyName = it)) },
            label = { Text(stringResource(R.string.keyword)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            //     enabled = enabled,
            singleLine = true
        )
    }
}

@Composable
fun TodoList(
    todos: List<Todo>,
    onTodoClick: (String) -> Unit,
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = todos, key = { it.id }) { initTodo ->
            TodoPhotoCard(
                todo = initTodo,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .pointerInput(onTodoClick) {
                        detectTapGestures {
                            onTodoClick(initTodo.id)
                            viewModel.onTodoClick(initTodo)
                        }
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PhotosListScreenPreview() {
    TodoNotionComposeTheme {
        val mockData = List(10) {
            Todo(
                id = "$it",
                pageURL = "https://pixabay.com/photos/christmas-baubles-heart-decoration-8455782/",
                type = "photo",
                tags = "christmas baubles, heart, laptop wallpaper",
                views = 8336,
                downloads = 7061,
                collections = 22,
                likes = 103,
                comments = 48,
                user_id = 1425977,
                user = "ChiemSeherin",
                userImageURL = "https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg",
                imgSrcUrl = "https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg"
            )
        }
        val todoViewModel: TodoViewModel =
            viewModel(factory = TodoViewModel.Factory)
        TodoList(mockData, onTodoClick = {}, viewModel = todoViewModel)
    }
}





