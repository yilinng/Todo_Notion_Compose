package com.example.todonotioncompose.ui.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todonotioncompose.R
import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.navigation.NavigationDestination
import com.example.todonotioncompose.ui.theme.*
import kotlinx.coroutines.launch


object LogoutDialogDestination : NavigationDestination {
    override val route = "logout"
    override val titleRes = R.string.logout
}

//https://stackoverflow.com/questions/67396976/use-dialog-as-navigation-destination-with-jetpack-compose
@Composable
fun LogoutDialog(
    userViewModel: UserViewModel,
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit
) {
    val tokenViewModel: TokenViewModel = viewModel(factory = AppViewModelProvider.Factory)
    //check have token
    val uiState by tokenViewModel.tokensUiState.collectAsState()
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
                        text = stringResource(id = R.string.logout_title),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                    Text(
                        text = stringResource(id = R.string.logout_cont),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )
                }
                Divider(color = Gray100)
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
                                Log.d(
                                    "logoutOk1",
                                    tokenViewModel.tokensUiState.value.itemList.toString()
                                )
                                // logout action reload home page
                                coroutineScope.launch {
                                    tokenViewModel.deleteToken()
                                    //init loginUiState
                                    userViewModel.initLoginUiState()
                                    //init token
                                    userViewModel.initToken()

                                    Log.d(
                                        "logoutOk2loginUiState",
                                        userViewModel.loginUiState.toString()
                                    )

                                    navigateToLogin()
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
