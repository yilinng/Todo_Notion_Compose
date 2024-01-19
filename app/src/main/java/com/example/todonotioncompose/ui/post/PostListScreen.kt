package com.example.todonotioncompose.ui.post

import android.util.Log
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
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.Keyword.Keyword
import com.example.todonotioncompose.model.Post
import com.example.todonotioncompose.model.Todo
import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.auth.PostUiState
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.theme.TodoNotionComposeTheme
import com.example.todonotioncompose.ui.todo.ErrorScreen
import com.example.todonotioncompose.ui.todo.LoadingScreen
import com.example.todonotioncompose.ui.todo.PhotosGridScreen
import com.example.todonotioncompose.ui.todo.TodoPhotoCard
import com.example.todonotioncompose.ui.todo.TodoUiState
import com.example.todonotioncompose.ui.todo.TodoViewModel
import kotlinx.coroutines.launch

object PostListScreenDestination : NavigationDestination {
    override val route = "postList"
    override val titleRes = R.string.posts
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    navigateToPostDetails: (String) -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    postUiState: PostUiState, retryAction: () -> Unit,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    //val uiState by viewModel.todoUiState

    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(PostListScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxHeight()
                .background(Color.LightGray)
        ) {

            when (postUiState) {
                is PostUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())
                is PostUiState.Success -> PostList(
                    posts = postUiState.posts, modifier = modifier.fillMaxWidth(),
                    onPostClick = navigateToPostDetails, userViewModel = userViewModel
                )

                is PostUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
            }

        }
    }
}



@Composable
fun PostList(
    posts: List<Post>,
    onPostClick: (String) -> Unit,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(items = posts, key = { it.id }) { initPost ->
            PostCard(
                post = initPost,
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen.padding_small))
                    .pointerInput(onPostClick) {
                        detectTapGestures {
                            onPostClick(initPost.id)
                            userViewModel.onPostClick(initPost)
                        }
                    }
            )
        }
    }
    Log.d("getPostsScreen", posts.toString())
}

@Composable
fun PostCard(
    post: Post,
    modifier: Modifier
){
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Text(text = post.title, fontSize = 15.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.padding(top=2.dp))

        Text(text = post.content, fontSize = 12.sp, color = Color.DarkGray,  textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.padding(top=2.dp))


        Text(text = post.userId, fontSize = 12.sp, color = Color.DarkGray,  textAlign = TextAlign.Center)

    }
}







