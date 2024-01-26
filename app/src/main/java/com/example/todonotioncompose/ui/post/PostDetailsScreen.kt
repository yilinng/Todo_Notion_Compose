package com.example.todonotioncompose.ui.post

import android.annotation.SuppressLint
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.todonotioncompose.R
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.dimensionResource

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.Token.Token

import com.example.todonotioncompose.model.Post
import com.example.todonotioncompose.ui.AppViewModelProvider

import com.example.todonotioncompose.ui.auth.PostUiState
import com.example.todonotioncompose.ui.auth.TokenViewModel
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.auth.toPost
import com.example.todonotioncompose.ui.theme.Gray100
import com.example.todonotioncompose.ui.theme.Gray200
import com.example.todonotioncompose.ui.theme.Green100
import com.example.todonotioncompose.ui.theme.Green200
import com.example.todonotioncompose.ui.todo.ErrorScreen
import com.example.todonotioncompose.ui.todo.LoadingScreen
import kotlinx.coroutines.launch

object PostDetailsScreenDestination : NavigationDestination {
    override val route = "postDetails"
    override val titleRes = R.string.post_details_label
    const val postIdArg = "postId"
    val routeWithArgs = "$route/{$postIdArg}"
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailsScreen(
    navigateToPostEdit: (String) -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
    postUiState: PostUiState, retryAction: () -> Unit,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    //val uiState by viewModel.todoUiState
    val uiState by userViewModel.post.collectAsState()
    val postId = userViewModel.post.collectAsState().value.id
    val haveToken = userViewModel.token.collectAsState().value

    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(PostDetailsScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateBack
            )
        }, floatingActionButton = {
            if (haveToken != null) {
                //if userId same as post userId, show edit button
                Log.d("postDetail_edit_token", haveToken.accessToken)
                Log.d("postDetail_edit_userID", haveToken.userId)
                Log.d("postDetail_edit_post_userID", uiState.userId)

                if (haveToken.accessToken.isNotEmpty() && haveToken.userId == uiState.userId) {

                    FloatingActionButton(onClick = {
                        //navigation to postEdit
                        navigateToPostEdit(postId)
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
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
                is PostUiState.Success -> PostDetailsBody(
                    post = uiState,
                    haveToken = haveToken!!,
                    userViewModel = userViewModel,
                    navigateBack = navigateBack,
                    modifier = modifier.fillMaxWidth()
                )

                is PostUiState.Error -> ErrorScreen(retryAction, modifier = modifier.fillMaxSize())
            }

        }
    }
}


//https://stackoverflow.com/questions/63801346/error-composable-invocations-can-only-happen-from-the-context-of-a-composabl
@SuppressLint("UnrememberedMutableState")
@Composable
fun PostDetailsBody(
    post: Post,
    haveToken: Token,
    userViewModel: UserViewModel,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    //update singlePostInputUiState
    userViewModel.updatePostInputUiState(post.toPost())

    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        var isClicked by remember { mutableStateOf(false) }

        PostDetails(post = post, modifier = Modifier.fillMaxWidth())

        //if userId same as post userId, show delete button
        if (haveToken.accessToken.isNotEmpty() && haveToken.userId == post.userId) {
            Button(
                onClick = {
                    isClicked = true
                },
                enabled = post.id.isNotEmpty(),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.delete))
            }
        }

        if(isClicked){
            DeleteDialog(postId = post.id, userViewModel = userViewModel, navigateBack = navigateBack)
        }
    }
}



@Composable
fun DeleteDialog(
    postId: String,
    userViewModel: UserViewModel,
    navigateBack: () -> Unit){
    // Creates a CoroutineScope bound to the MoviesScreen's lifecycle
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.width(280.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Green200)
                .padding(bottom = 3.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Green100),
        ) {
            Column {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.delete_post_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(id = R.string.delete_post_cont),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
                androidx.compose.material.Divider(color = Gray100)
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                // dismiss dialog
                                navigateBack()
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                            .background(Gray200),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {

                                // logout action reload home page
                                coroutineScope.launch {
                                    userViewModel.deletePostAction(postId)

                                    Log.d(
                                        "postDetail_delete",
                                        userViewModel.postUiState.toString()
                                    )

                                 //navigate to last page
                                    navigateBack()
                                }

                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.ok),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    }
                }
            }
        }
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











