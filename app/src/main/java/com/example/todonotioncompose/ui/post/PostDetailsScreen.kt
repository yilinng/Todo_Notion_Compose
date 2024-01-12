package com.example.todonotioncompose.ui.post

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import com.example.todonotioncompose.R
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.dimensionResource

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight

import com.example.todonotioncompose.TodoNotionAppBar

import com.example.todonotioncompose.model.Post

import com.example.todonotioncompose.ui.auth.PostUiState
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.todo.ErrorScreen
import com.example.todonotioncompose.ui.todo.LoadingScreen

object PostDetailsScreenDestination : NavigationDestination {
    override val route = "postDetails"
    override val titleRes = R.string.post_details_label
    const val postIdArg = "postId"
    val routeWithArgs = "$route/{$postIdArg}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    postUiState: PostUiState, retryAction: () -> Unit,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    //val uiState by viewModel.todoUiState
    val uiState by userViewModel.post.collectAsState()

    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(PostDetailsScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateBack
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
                is PostUiState.Success -> PostDetailsBody(
                    post = uiState,
                    modifier = modifier.fillMaxWidth()
                )

                is PostUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
            }

        }
    }
}


@Composable
fun PostDetailsBody(
    post: Post,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        PostDetails(post = post, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun PostDetails(
    post: Post, modifier: Modifier = Modifier
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

           PostDetailsRow(
                labelResID = R.string.todo_detail_title,
                itemDetail = post.title,
                modifier = Modifier.padding(
                    horizontal = dimensionResource(
                        id = R.dimen
                            .padding_medium
                    )
                )
            )

            PostDetailsRow(
                labelResID = R.string.username,
                itemDetail = post.content,
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
private fun PostDetailsRow(
    @StringRes labelResID: Int, itemDetail: String, modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Text(text = stringResource(labelResID))
        Spacer(modifier = Modifier.weight(1f))
        Text(text = itemDetail, fontWeight = FontWeight.Bold)
    }
}









