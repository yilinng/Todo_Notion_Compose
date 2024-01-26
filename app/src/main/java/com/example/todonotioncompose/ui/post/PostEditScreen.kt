package com.example.todonotioncompose.ui.post

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

import com.example.todonotioncompose.R
import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.ui.auth.SinglePostUiState
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.auth.showMessage
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import com.example.todonotioncompose.ui.theme.Gray50

object PostEditDestination : NavigationDestination {
    override val route = "post_edit"
    override val titleRes = R.string.edit_post
    const val postIdArg = "postId"
    val routeWithArgs = "$route/{$postIdArg}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostEditScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    userViewModel: UserViewModel,
    singlePostUiState: SinglePostUiState
) {
    //  val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(PostEditDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxHeight()
                .background(Gray50)

        ) {
            val context = LocalContext.current
            val postId = userViewModel.post.collectAsState().value.id

            PostEntryBody(
                postUiState = userViewModel.singlePostInputUiState,
                onPostValueChange = userViewModel::updatePostInputUiState,
                onSaveClick = {
                    // Note: If the user rotates the screen very fast, the operation may get cancelled
                    // and the item may not be saved in the Database. This is because when config
                    // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                    // be cancelled - since the scope is bound to composition.
                    userViewModel.checkEditPost(
                        postId,
                        userViewModel.singlePostInputUiState.postDetails
                    )
                    Log.d("postState", userViewModel.singlePostUiState.toString())

                },
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            )

            when (singlePostUiState) {
                is SinglePostUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier
                        .width(80.dp)
                        .zIndex(2f),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                is SinglePostUiState.Success -> PostProcess(
                    post = singlePostUiState.post,
                    navigateBack = navigateBack,
                    userViewModel = userViewModel
                )

                is SinglePostUiState.Error -> {
                    Text(
                        text = stringResource(R.string.edit_error),
                        color = Color.Red,
                        fontSize = 20.sp
                    )
                    showMessage(context = context, errorText = R.string.edit_error)
                }

                else -> Unit
            }
        }
    }
}



