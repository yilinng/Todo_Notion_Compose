package com.example.todonotioncompose.ui.auth

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog

import androidx.compose.material3.Button

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todonotioncompose.TodoNotionAppBar
import com.example.todonotioncompose.data.Token.Token

import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.theme.*

import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.ui.window.Dialog


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
    signupUiState: SignupUiState,
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
                .background(Gray50)
        ) {
           //var errorText by mutableStateOf("")
           // var loading by remember { mutableStateOf(false) }
            val context = LocalContext.current

            SignupEntryBody(
                signupInputUiState = userViewModel.signupInputUiState,
                //    onLoginValueChange = userViewModel::up,
                onSaveClick = {
                    userViewModel.checkSignup(userViewModel.signupInputUiState.signupDetails)
                },
                onLoginTextClick = {
                    navigateBack()
                },
                userViewModel = userViewModel,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
            )


            when(signupUiState){
                is SignupUiState.Loading -> CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
                is SignupUiState.Success -> SignupProcess(
                    responseBody = signupUiState.responseBody,
                    navigateBack = navigateBack,
                )
                is SignupUiState.Error ->  {
                    Text(text = signupUiState.errorText, color = Color.Red, fontSize = 20.sp)

                    showMessage(context = context, errorText = R.string.login_error)

                }

            }

        }
    }
}

@Composable
fun SignupProcess(
    responseBody: ResponseBody,
    navigateBack: () -> Unit,
) {

    Log.d("signupSuccess", responseBody.toString())

    AlertDialog(
        icon = {
            Icon(Icons.Default.CheckCircleOutline, contentDescription = "Success Icon")
        },
        title = {
            Text(text = stringResource(R.string.signup_success_msg1))
        },
        text = {
            Text(text =  stringResource(R.string.signup_success_msg2))
        },
        onDismissRequest = {
           // onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    navigateBack()
                }
            ) {
                Text(stringResource(R.string.signup_success_dialogs_btn))
            }
        },

    )
  //  tokenViewModel.updateUiState(token.toToken())
    /*
    LaunchedEffect(tokenViewModel) {
       // tokenViewModel.saveToken()
        userViewModel.initSignup()
        navigateToHome()
    }
    */
}

@Composable
fun SignupEntryBody(
    signupInputUiState: SignupInputUiState,
    // onLoginValueChange: (LoginDetails) -> Unit,
    onLoginTextClick:() -> Unit,
    onSaveClick: (SignupDetails) -> Unit,
    userViewModel: UserViewModel,
    modifier: Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
       SignupInputForm(
           signupDetails = signupInputUiState.signupDetails,
           onSignupValueChange = userViewModel::updateSignupUiState
       )

       // Spacer(modifier = modifier.padding(1.dp))

        Button(
            onClick = { onSaveClick(signupInputUiState.signupDetails) },
            enabled = signupInputUiState.isEntryValid,
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.signup))
        }
  //      Spacer(modifier = modifier.padding(1.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(text = stringResource(R.string.signup_login_text), color = Sky400)
//            Spacer(modifier = Modifier.padding(end=2.dp) )

            TextButton(
                onClick = { onLoginTextClick() }
            ) {
                Text(text = stringResource(R.string.login), color = Pink80)
            }
        }
    }
}

@Composable
fun SignupInputForm(
    signupDetails: SignupDetails,
    modifier: Modifier = Modifier,
    onSignupValueChange: (SignupDetails) -> Unit = {},
    enabled: Boolean = true
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium))
    ) {
        OutlinedTextField(
            value = signupDetails.name,
            onValueChange = { onSignupValueChange(signupDetails.copy(name = it)) },
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
            onValueChange = { onSignupValueChange(signupDetails.copy(username = it)) },
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
            onValueChange = { onSignupValueChange(signupDetails.copy(email = it)) },
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
            onValueChange = { onSignupValueChange(signupDetails.copy(password = it)) },
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





