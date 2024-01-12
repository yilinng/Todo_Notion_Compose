package com.example.todonotioncompose.ui.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.example.todonotioncompose.R
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.UsersRepository
import com.example.todonotioncompose.model.Todo
import com.example.todonotioncompose.ui.AppViewModelProvider
import kotlinx.coroutines.launch
import com.example.todonotioncompose.ui.theme.*
import com.example.todonotioncompose.ui.todo.PhotosGridScreen
import com.example.todonotioncompose.ui.todo.TodoViewModel


object LoginScreenDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.login
}


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
    tokenViewModel: TokenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    userViewModel: UserViewModel,
    navigateToHome: () -> Unit,
    navigateToSignup: () -> Unit,
    loginUiState: LoginUiState,
    modifier: Modifier = Modifier
) {
    //val uiState by viewModel.todoUiState
    val coroutineScope = rememberCoroutineScope()
    val uiState by tokenViewModel.tokensUiState.collectAsState()


    Scaffold(
        topBar = {
            TodoNotionAppBar(
                title = stringResource(LoginScreenDestination.titleRes),
                canNavigateBack = canNavigateBack,
                navigateUp = onNavigateUp
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Gray50)
        ) {
            var errorText by mutableStateOf("")
            var loading by remember { mutableStateOf(false) }

            LoginEntryBody(
                loginInputUiState = userViewModel.loginInputUiState,
                //    onLoginValueChange = userViewModel::up,
                onSaveClick = {

                    coroutineScope.launch {
                        userViewModel.checkLogin(userViewModel.loginInputUiState.loginDetails)

                        when(loginUiState) {
                            is LoginUiState.Loading -> {
                                loading = true
                            }
                            is LoginUiState.Success -> {
                                loading = false
                                navigateToHome()
                                tokenViewModel.updateUiState(loginUiState.token.toToken())
                                tokenViewModel.saveToken()
                            }
                            is LoginUiState.Error -> {
                                loading = false
                                errorText = LoginUiState.Error.toString()
                            }
                        }

                    }
                },
                onSignupTextClick = {
                    navigateToSignup()
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
fun LoginEntryBody(
    loginInputUiState: LoginInputUiState,
    // onLoginValueChange: (LoginDetails) -> Unit,
    onSignupTextClick:() -> Unit,
    onSaveClick: (LoginDetails) -> Unit,
    errorText: String,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        LoginInputForm(loginDetails = loginInputUiState.loginDetails)

        Spacer(modifier = modifier.padding(1.dp))

        Text(text = errorText, color = Color.Red, fontSize = 20.sp)

        Button(
            onClick = { onSaveClick },
            enabled = loginInputUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.login))
        }

        Spacer(modifier = modifier.padding(1.dp))

        Row {

            Text(text = stringResource(R.string.login_signup_text), color = Green400)

            Spacer(modifier = Modifier.padding(end=2.dp) )

            TextButton(
                onClick = { onSignupTextClick }
            ) {
                Text(text = stringResource(R.string.signup), color = Pink80)
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginInputForm(
    loginDetails: LoginDetails,
    modifier: Modifier = Modifier,
    onValueChange: (LoginDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = loginDetails.usernameOrEmail,
            onValueChange = { onValueChange(loginDetails.copy(usernameOrEmail = it)) },
            label = { Text(stringResource(R.string.usernameOrEmail)) },
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
            value = loginDetails.password,
            onValueChange = { onValueChange(loginDetails.copy(password = it)) },
            label = { Text(stringResource(R.string.login_password)) },
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






