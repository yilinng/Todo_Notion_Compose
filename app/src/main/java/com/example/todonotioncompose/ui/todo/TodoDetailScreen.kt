package com.example.todonotioncompose.ui.todo

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.todonotioncompose.R
import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.todonotioncompose.model.Todo
import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.ui.theme.TodoNotionComposeTheme

object TodoDetailScreenDestination : NavigationDestination {
    override val route = "todo_details"
    override val titleRes = R.string.todo_detail_title
    const val todoIdArg = "todoId"
    val routeWithArgs = "$route/{$todoIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoDetailScreen(
    navigateBack: () -> Unit,
    viewModel: TodoViewModel,
    modifier: Modifier = Modifier,
) {
    //https://stackoverflow.com/questions/38250022/what-does-by-keyword-do-in-kotlin
   // val uiState by viewModel.todo.collectAsState()
    val uiState by viewModel.todo.collectAsState()

    //val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(TodoDetailScreenDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },  modifier = modifier
    ) { innerPadding ->
        TodoDetailsBody(
            todo = uiState,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        )
    }

    Log.d("todoClickScreen", uiState.toString())

}

@Composable
private fun TodoDetailsBody(
    todo: Todo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        TodoDetails(
            todo = todo, modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun TodoDetails(
    todo: Todo, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current).data(todo.imgSrcUrl)
                    .crossfade(true).build(),
                contentDescription = stringResource(R.string.todo_img),
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(200.dp)
                //contentScale = ContentScale.Fit,
                //modifier = Modifier.fillMaxWidth()
               // modifier = Modifier.aspectRatio(16f / 6f)
            )
            TodoDetailsRow(
                labelResID = R.string.todo_detail_title,
                itemDetail = todo.tags,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )

            TodoDetailsRow(
                labelResID = R.string.username,
                itemDetail = todo.user,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )
        }

    }
}

@Composable
private fun TodoDetailsRow(
    @StringRes labelResID: Int, itemDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = itemDetail, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailsScreenPreview() {
    val todoViewModel: TodoViewModel =
        viewModel(factory = TodoViewModel.Factory)
    TodoNotionComposeTheme {
        TodoDetailsBody(todo =  todoViewModel.todo.collectAsState().value)
    }
}