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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.todonotioncompose.R
import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.model.Post

import com.example.todonotioncompose.ui.auth.PostDetails
import com.example.todonotioncompose.ui.auth.SinglePostInputUiState
import com.example.todonotioncompose.ui.auth.SinglePostUiState
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.auth.showMessage
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import com.example.todonotioncompose.ui.theme.*
import com.example.todonotioncompose.ui.theme.TodoNotionComposeTheme
import kotlinx.coroutines.delay

object PostEntryDestination : NavigationDestination {
    override val route = "post_entry"
    override val titleRes = R.string.post_entry_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostEntryScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    userViewModel: UserViewModel,
    singlePostUiState: SinglePostUiState
) {
    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(PostEntryDestination.titleRes),
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
                .background(Green50)

        ) {
            val context = LocalContext.current

            PostEntryBody(
                postUiState = userViewModel.singlePostInputUiState,
                onPostValueChange = userViewModel::updatePostInputUiState,
                onSaveClick = {
                    // Note: If the user rotates the screen very fast, the operation may get cancelled
                    // and the item may not be saved in the Database. This is because when config
                    // change occurs, the Activity will be recreated and the rememberCoroutineScope will
                    // be cancelled - since the scope is bound to composition.
                    userViewModel.checkAddPost(userViewModel.singlePostInputUiState.postDetails)
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
                    color = Green300,
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

@Composable
fun PostProcess(
    post: Post,
    userViewModel: UserViewModel,
    navigateBack: () -> Unit
) {

    Log.d("post action Success", post.toString())
    //update post
    userViewModel.onPostClick(post)
    //https://medium.com/@banmarkovic/jetpack-compose-clear-back-stack-popbackstack-inclusive-explained-14ee73a29df5
    LaunchedEffect(userViewModel) {
        //init post Input Ui
        userViewModel.initPost()
        //init singlePostUiState
        userViewModel.initSignPostUiState()
        //https://stackoverflow.com/questions/54360151/android-navigation-popbackstack
        //https://developer.android.com/guide/navigation/backstack?hl=en
        navigateBack()
    }

}

@Composable
fun PostEntryBody(
    postUiState: SinglePostInputUiState,
    onPostValueChange: (PostDetails) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_large))
    ) {
        PostInputForm(
            postDetails = postUiState.postDetails,
            onValueChange = onPostValueChange,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onSaveClick,
            enabled = postUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.save))
        }
    }
}

//https://stackoverflow.com/questions/69040571/android-compose-textfield-how-to-set-exact-3-lines
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostInputForm(
    postDetails: PostDetails,
    modifier: Modifier = Modifier,
    onValueChange: (PostDetails) -> Unit = {},
    enabled: Boolean = true
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = postDetails.title,
            onValueChange = { onValueChange(postDetails.copy(title = it)) },
            label = { Text(stringResource(R.string.post_title)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    // do something here
                })
        )
        OutlinedTextField(
            value = postDetails.content,
            onValueChange = { onValueChange(postDetails.copy(content = it)) },
            label = { Text(stringResource(R.string.post_content)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            minLines = 3,
            maxLines = 3,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    // do something here
                })
        )
        if (enabled) {
            Text(
                text = stringResource(R.string.required_fields),
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_medium))
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ItemEntryScreenPreview() {
    TodoNotionComposeTheme {
        PostEntryBody(postUiState = SinglePostInputUiState(
            PostDetails(
                title = "Item name", content = "test content....."
            )
        ), onPostValueChange = {}, onSaveClick = {})
    }
}