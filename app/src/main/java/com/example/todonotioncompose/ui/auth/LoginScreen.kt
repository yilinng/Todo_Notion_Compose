package com.example.todonotioncompose.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import com.example.todonotioncompose.R
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.material3.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.Token.Token

import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.theme.*


object LoginScreenDestination : NavigationDestination {
    override val route = "login"
    override val titleRes = R.string.login
}

//https://developer.android.com/jetpack/compose/side-effects
//https://developer.android.com/jetpack/compose/animation/quick-guide
//https://stackoverflow.com/questions/69582190/how-to-do-this-scroll-hide-fab-button-in-jetpack-compose-with-transaction
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    tokenViewModel: TokenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    userViewModel: UserViewModel,
    navigateToHome: () -> Unit,
    navigateToSignup: () -> Unit,
    loginUiState: LoginUiState,
    modifier: Modifier = Modifier
) {
    // val uiState by tokenViewModel.tokensUiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight()
            .background(Gray50)
            .verticalScroll(rememberScrollState())
    ) {
        //https://developer.android.com/jetpack/compose/state
        // https://developer.android.com/codelabs/jetpack-compose-state?index=..%2F..index#0
        //https://developer.android.com/kotlin/coroutines
        //https://stackoverflow.com/questions/69404720/how-to-propagate-the-response-of-an-async-operation-to-the-view-using-jetpack-co
        //  var errorText by remember { mutableStateOf("") }
        //  var loading by remember { mutableStateOf(false) }
        // Fetching the local context for using the Toast
        val context = LocalContext.current
        val columnPadding = dimensionResource(id = R.dimen.padding_medium)

        Text(
            text = stringResource(R.string.login),
            color = Sky600,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        )

        LoginEntryBody(
            loginInputUiState = userViewModel.loginInputUiState,
            //    onLoginValueChange = userViewModel::up,
            onSaveClick = {
                userViewModel.checkLogin(userViewModel.loginInputUiState.loginDetails)
                Log.d("loginState", loginUiState.toString())
            },
            onSignupTextClick = navigateToSignup,
            userViewModel = userViewModel,
            modifier = Modifier
                .fillMaxWidth()
        )

        when (loginUiState) {
            is LoginUiState.Loading -> CircularProgressIndicator(
                modifier = Modifier
                    .width(64.dp)
                    .zIndex(2f),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            is LoginUiState.Success -> LoginProcess(
                token = loginUiState.token,
                tokenViewModel = tokenViewModel,
                navigateToHome = navigateToHome,
                userViewModel = userViewModel
            )

            is LoginUiState.Error -> {
                Text(
                    text = stringResource(R.string.login_error),
                    color = Color.Red,
                    fontSize = 20.sp,
                    modifier = modifier
                        .offset(y = -(columnPadding))
                )
                showMessage(context = context, errorText = R.string.login_error)
            }

            else -> Unit

        }

    }


}


fun showMessage(context: Context, errorText: Int) {
    Toast.makeText(context, errorText, Toast.LENGTH_SHORT).show()
}

@Composable
fun LoginProcess(
    token: Token,
    tokenViewModel: TokenViewModel,
    navigateToHome: () -> Unit,
    userViewModel: UserViewModel
) {

    Log.d("loginSuccess", token.toToken().toString())
    tokenViewModel.updateUiState(token.toToken())
    userViewModel.setToken(token)
    LaunchedEffect(tokenViewModel) {
        tokenViewModel.saveToken()
        userViewModel.initLogin()
        navigateToHome()
    }

}


@Composable
fun LoginEntryBody(
    loginInputUiState: LoginInputUiState,
    onSignupTextClick: () -> Unit,
    onSaveClick: (LoginDetails) -> Unit,
    userViewModel: UserViewModel,
    modifier: Modifier
) {
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        LoginInputForm(
            loginDetails = loginInputUiState.loginDetails,
            onLoginValueChange = userViewModel::updateLoginUiState
        )

        Button(
            onClick = {
                onSaveClick(loginInputUiState.loginDetails)
            },
            enabled = loginInputUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.login))
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = stringResource(R.string.login_signup_text), color = Green400)
            TextButton(
                onClick = { onSignupTextClick() }
            ) {
                Text(text = stringResource(R.string.signup), color = Pink40)
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginInputForm(
    loginDetails: LoginDetails,
    modifier: Modifier = Modifier,
    onLoginValueChange: (LoginDetails) -> Unit = {},
    enabled: Boolean = true
) {
    var usernameOrEmailIsError by rememberSaveable { mutableStateOf(false) }
    var passwordIsError by rememberSaveable { mutableStateOf(false) }

    val usernameOrEmailError = R.string.login_usernameOrEmail
    val passwordError = R.string.login_password
    val keyboardController = LocalSoftwareKeyboardController.current
    var passwordHidden by rememberSaveable { mutableStateOf(true) }

    fun validateUsernameOrEmail(text: String) {
        usernameOrEmailIsError = text.isEmpty()
    }

    fun validatePassword(text: String) {
        passwordIsError = text.isEmpty()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = loginDetails.usernameOrEmail,
            onValueChange = { onLoginValueChange(loginDetails.copy(usernameOrEmail = it)) },
            label = { Text(stringResource(R.string.usernameOrEmail)) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    // Provide localized description of the error
                    if (usernameOrEmailIsError) error(usernameOrEmailError)
                },
            enabled = enabled,
            singleLine = true,
            isError = usernameOrEmailIsError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    // do something here
                }
            ) { validateUsernameOrEmail(loginDetails.usernameOrEmail) },
        )
        OutlinedTextField(
            value = loginDetails.password,
            onValueChange = { onLoginValueChange(loginDetails.copy(password = it)) },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation =
            if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    // Provide localized description of the error
                    if (passwordIsError) error(passwordError)
                },
            enabled = enabled,
            singleLine = true,
            isError = passwordIsError,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                // do something here
            }) { validatePassword(loginDetails.password) },
            trailingIcon = {
                IconButton(onClick = { passwordHidden = !passwordHidden }) {
                    val visibilityIcon =
                        if (passwordHidden) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    // Please provide localized description for accessibility services
                    val description = if (passwordHidden) "Show password" else "Hide password"
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }

        )

    }
}






