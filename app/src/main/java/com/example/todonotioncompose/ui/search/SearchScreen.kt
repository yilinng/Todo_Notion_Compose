package com.example.todonotioncompose.ui.search

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todonotioncompose.R
import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.Keyword.Keyword
import com.example.todonotioncompose.model.Todo
import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import com.example.todonotioncompose.ui.theme.*
import com.example.todonotioncompose.ui.theme.TodoNotionComposeTheme
import com.example.todonotioncompose.ui.todo.TodoViewModel
import kotlinx.coroutines.launch


object SearchScreenDestination : NavigationDestination {
    override val route = "search_todo"
    override val titleRes = R.string.search_btn
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navigateToSearchResult: () -> Unit,
    navigateBack: () -> Unit,
    searchViewModel: SearchViewModel,
    viewModel: TodoViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by searchViewModel.searchUiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .background(color = Gray50)
    ) {
        KeywordEntryBody(
            keywordUiState = searchViewModel.keywordUiState,
            onKeywordValueChange = searchViewModel::updateUiState,
            searchViewModel = searchViewModel,
            viewModel = viewModel,
            navigateToSearchResult = navigateToSearchResult,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = 1.dp))

        KeywordList(
            keywords = uiState.itemList,
            searchViewModel = searchViewModel,
            viewModel = viewModel,
            navigateToSearchResult = navigateToSearchResult,
            onDelete = {
                // Note: If the user rotates the screen very fast, the operation may get cancelled
                // and the item may not be deleted from the Database. This is because when config
                // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                // be cancelled - since the scope is bound to composition.
                coroutineScope.launch {
                    searchViewModel.deleteKeyword()
                    searchViewModel.initKeyword()
                    navigateBack()
                }
            },
        )
    }

}

//https://stackoverflow.com/questions/59133100/how-to-close-the-virtual-keyboard-from-a-jetpack-compose-textfield
@Composable
fun KeywordEntryBody(
    keywordUiState: KeywordUiState,
    onKeywordValueChange: (KeywordDetails) -> Unit,
    searchViewModel: SearchViewModel,
    viewModel: TodoViewModel,
    navigateToSearchResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_small)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
    ) {
        OutlinedTextField(
            value = keywordUiState.keywordDetails.keyName.lowercase(),
            onValueChange = { onKeywordValueChange(keywordUiState.keywordDetails.copy(keyName = it)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (keywordUiState.keywordDetails.keyName.isNotEmpty()) {
                    coroutineScope.launch {
                        if (searchViewModel.filterByKeyWordsCount(keywordUiState.keywordDetails.keyName.lowercase()) == 0) {
                            searchViewModel.saveKeyword()
                        }
                        viewModel.getPhotosByKeyWord(keywordUiState.keywordDetails.keyName)
                        navigateToSearchResult()
                    }
                }
            }),
            label = { Text(stringResource(R.string.keyword)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            //     enabled = enabled,
            singleLine = true,
        )
    }
}

//https://developer.android.com/jetpack/compose/touch-input/pointer-input/tap-and-press
@Composable
fun KeywordList(
    keywords: List<Keyword>,
    viewModel: TodoViewModel,
    navigateToSearchResult: () -> Unit,
    modifier: Modifier = Modifier,
    searchViewModel: SearchViewModel,
    onDelete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
    ) {
        items(items = keywords, key = { it.id }) { keyword ->
            SearchKeyword(
                keyword = keyword,
                onDelete = onDelete,
                searchViewModel = searchViewModel,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .pointerInput(searchViewModel) {
                        detectTapGestures {
                            coroutineScope.launch {
                                searchViewModel.updateUiState(keyword.toKeyword())
                                Log.d(
                                    "search_click",
                                    searchViewModel.keywordUiState.keywordDetails.toString()
                                )
                                viewModel.getPhotosByKeyWord(searchViewModel.keywordUiState.keywordDetails.keyName)
                                navigateToSearchResult()
                            }
                        }
                    }
            )

        }
    }
}

//https://stackoverflow.com/questions/64425848/how-to-set-spacer-between-with-two-elements-in-a-row
@Composable
private fun SearchKeyword(
    keyword: Keyword,
    onDelete: () -> Unit,
    searchViewModel: SearchViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        var deleteConfirmationRequired by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = keyword.keyName,
                    style = MaterialTheme.typography.titleLarge,
                )

                IconToggleButton(
                    checked = deleteConfirmationRequired,
                    onCheckedChange = { deleteConfirmationRequired = it }) {
                    if (deleteConfirmationRequired) {
                        Icon(Icons.Filled.Close, contentDescription = "Clean description")
                    } else {
                        Icon(Icons.Outlined.Close, contentDescription = "Clean description")
                    }
                }

                if (deleteConfirmationRequired) {
                    DeleteConfirmationDialog(
                        onDeleteConfirm = {
                            searchViewModel.updateUiState(keyword.toKeyword())
                            deleteConfirmationRequired = false
                            onDelete()

                        },
                        onDeleteCancel = { deleteConfirmationRequired = false },
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium))
                    )
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit, onDeleteCancel: () -> Unit, modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.delete_post_title)) },
        text = { Text(stringResource(R.string.delete_post_cont)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(text = stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(text = stringResource(R.string.ok))
            }
        })
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    TodoNotionComposeTheme {
        val mockTodo = Todo(
            id = "1",
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
        val mockKeyword = Keyword(id = 123, keyName = "Test keyword")
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
        val searchViewModel: SearchViewModel =
            viewModel(factory = AppViewModelProvider.Factory)
        val todoViewModel: TodoViewModel =
            viewModel(factory = TodoViewModel.Factory)
        SearchScreen(
            navigateBack = {},
            navigateToSearchResult = { mockKeyword },
            searchViewModel = searchViewModel,
            viewModel = todoViewModel
        )
    }
}