package com.example.todonotioncompose.ui.todo

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.todonotioncompose.R
import com.example.todonotioncompose.model.Todo
import com.example.todonotioncompose.ui.theme.TodoNotionComposeTheme
import com.example.todonotioncompose.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route = "home"
    override val titleRes = R.string.app_name
}

@Composable
fun TodoListScreen(
    navigateToTodoDetails: (String) -> Unit,
    viewModel: TodoViewModel,
    todoUiState: TodoUiState, retryAction: () -> Unit, modifier: Modifier = Modifier
) {
    when (todoUiState) {
        is TodoUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
        is TodoUiState.Success -> PhotosGridScreen(
            todos = todoUiState.todos, modifier = modifier.fillMaxWidth(),
            onTodoClick = navigateToTodoDetails, viewModel = viewModel
        )

        is TodoUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
    }
}

/**
 * The home screen displaying the loading message.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )

}


/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_connection_error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction) {
            Text(stringResource(R.string.retry))
        }
    }
}

/**
 * The home screen displaying photo grid.
 * https://developer.android.com/jetpack/compose/touch-input/pointer-input/tap-and-press
 */
@Composable
fun PhotosGridScreen(todos: List<Todo>, onTodoClick: (String) -> Unit, viewModel: TodoViewModel, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = modifier,
        contentPadding = PaddingValues(4.dp)
    ) {
        items(items = todos, key = { todo -> todo.id }) { todo ->
            TodoPhotoCard(
                todo = todo,
                modifier = modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
                   // .clickable { onTodoClick(todo.id) }
                    .pointerInput(onTodoClick){
                        detectTapGestures {
                            onTodoClick(todo.id)
                            viewModel.onTodoClick(todo)
                        }
                    }
            )
        }
    }
}

//https://developer.android.com/jetpack/compose/graphics/images/customize
//https://developer.android.com/jetpack/compose/text/style-text
@Composable
fun TodoPhotoCard(todo: Todo, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current).data(todo.imgSrcUrl)
                .crossfade(true).build(),
            error = painterResource(R.drawable.ic_broken_image),
            placeholder = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.todo_img),
            contentScale = ContentScale.Crop,
            modifier = Modifier.height(200.dp)
        )
        Text(text = todo.tags.replace(",", " "), fontSize = 15.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.padding(top=2.dp))

        Text(text = todo.user, fontSize = 12.sp, color = Color.DarkGray,  textAlign = TextAlign.Center)

    }
}


@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    TodoNotionComposeTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    TodoNotionComposeTheme {
        ErrorScreen({})
    }
}

@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    TodoNotionComposeTheme {
        val mockData = List(10) { Todo(id="$it", pageURL="https://pixabay.com/photos/christmas-baubles-heart-decoration-8455782/",
            type="photo",tags="christmas baubles, heart, laptop wallpaper", views=8336,downloads=7061,collections=22,likes=103,comments=48,user_id=1425977
            ,user="ChiemSeherin", userImageURL="https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg", imgSrcUrl="https://cdn.pixabay.com/user/2023/11/03/18-31-17-586_250x250.jpeg")}
        val todoViewModel: TodoViewModel =
            viewModel(factory = TodoViewModel.Factory)
        PhotosGridScreen(mockData, onTodoClick= {}, viewModel= todoViewModel)
    }
}