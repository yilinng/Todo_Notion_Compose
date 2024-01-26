package com.example.todonotioncompose.ui.post

import android.util.Log
import androidx.compose.foundation.background
import com.example.todonotioncompose.R
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.Token.Token

import com.example.todonotioncompose.model.Post

import com.example.todonotioncompose.ui.auth.PostUiState
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.todo.ErrorScreen
import com.example.todonotioncompose.ui.todo.LoadingScreen


object PostListScreenDestination : NavigationDestination {
    override val route = "postList"
    override val titleRes = R.string.posts
}

//https://stackoverflow.com/questions/66546962/jetpack-compose-how-do-i-refresh-a-screen-when-app-returns-to-foreground
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostListScreen(
    navigateToPostDetails: (String) -> Unit,
    navigateToPostEntry: () -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    postUiState: PostUiState, retryAction: () -> Unit,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    //val uiState by viewModel.todoUiState
    val haveToken = userViewModel.token.collectAsState().value

    Log.d("postList_token", haveToken.toString())

    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(PostListScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }, floatingActionButton = {
            if (haveToken != null) {
                if (haveToken.accessToken.isNotEmpty()) {
                    FloatingActionButton(onClick = {
                        //init singlePostUi
                        userViewModel.initPost()
                        //navigation to emptyEntry
                        navigateToPostEntry()

                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            }
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
                userViewModel = userViewModel,
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
    userViewModel: UserViewModel,
    modifier: Modifier
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {

        Text(text = userViewModel.slicePostTitle(post.title), fontSize = 15.sp, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.padding(top = 2.dp))

        Text(
            text = userViewModel.slicePostContent(post.content),
            fontSize = 12.sp,
            color = Color.DarkGray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(top = 2.dp))

    }
}









