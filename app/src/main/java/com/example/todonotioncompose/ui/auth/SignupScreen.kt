package com.example.todonotioncompose.ui.auth

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.Keyword.Keyword
import com.example.todonotioncompose.model.Todo
import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.theme.Green400
import com.example.todonotioncompose.ui.theme.Pink80
import com.example.todonotioncompose.ui.theme.Sky400
import com.example.todonotioncompose.ui.theme.TodoNotionComposeTheme
import com.example.todonotioncompose.ui.todo.ErrorScreen
import com.example.todonotioncompose.ui.todo.LoadingScreen
import com.example.todonotioncompose.ui.todo.PhotosGridScreen
import com.example.todonotioncompose.ui.todo.TodoPhotoCard
import com.example.todonotioncompose.ui.todo.TodoUiState
import com.example.todonotioncompose.ui.todo.TodoViewModel
import kotlinx.coroutines.launch

object SignupScreenDestination : NavigationDestination {
    override val route = "signup"
    override val titleRes = R.string.signup
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    tokenViewModel: TokenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    userViewModel: UserViewModel,
    navigateToHome: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    //val uiState by viewModel.todoUiState
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(SignupScreenDestination.titleRes),
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
            var errorText by mutableStateOf("")
            var loading by remember { mutableStateOf(false) }

            SignupEntryBody(
                signupInputUiState = userViewModel.signupInputUiState,
                //    onLoginValueChange = userViewModel::up,
                onSaveClick = {
                    coroutineScope.launch {
                        userViewModel.checkSignup(userViewModel.signupInputUiState.signupDetails)

                        when(userViewModel.signupUiState){
                            is SignupUiState.Loading -> {
                                loading = true
                            }
                            is SignupUiState.Success -> {
                                loading = false
                                navigateToHome()
                            }
                            is SignupUiState.Error -> {
                                loading = false
                                errorText = LoginUiState.Error.toString()
                            }
                        }
                    }
                },
                onLoginTextClick = {
                    navigateBack()
                },
                errorText = errorText,
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            )

            if(loading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

        }
    }
}

@Composable
fun SignupEntryBody(
    signupInputUiState: SignupInputUiState,
    // onLoginValueChange: (LoginDetails) -> Unit,
    onLoginTextClick:() -> Unit,
    onSaveClick: (LoginDetails) -> Unit,
    errorText: String,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
       SignupInputForm(signupDetails = signupInputUiState.signupDetails)

        Spacer(modifier = modifier.padding(1.dp))

        Text(text = errorText, color = Color.Red, fontSize = 20.sp)

        Button(
            onClick = { onSaveClick },
            enabled = signupInputUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.signup))
        }

        Spacer(modifier = modifier.padding(1.dp))

        Row {

            Text(text = stringResource(R.string.signup_login_text), color = Sky400)

            Spacer(modifier = Modifier.padding(end=2.dp) )

            TextButton(
                onClick = { onLoginTextClick }
            ) {
                Text(text = stringResource(R.string.signup), color = Pink80)
            }
        }
    }
}

@Composable
fun SignupInputForm(
    signupDetails: SignupDetails,
    modifier: Modifier = Modifier,
    onValueChange: (SignupDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = signupDetails.name,
            onValueChange = { onValueChange(signupDetails.copy(name = it)) },
            label = { Text(stringResource(R.string.name)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = signupDetails.username,
            onValueChange = { onValueChange(signupDetails.copy(username = it)) },
            label = { Text(stringResource(R.string.username)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )
        OutlinedTextField(
            value = signupDetails.email,
            onValueChange = { onValueChange(signupDetails.copy(email = it)) },
            label = { Text(stringResource(R.string.email)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

        OutlinedTextField(
            value = signupDetails.password,
            onValueChange = { onValueChange(signupDetails.copy(password = it)) },
            label = { Text(stringResource(R.string.password)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            singleLine = true
        )

    }
}





