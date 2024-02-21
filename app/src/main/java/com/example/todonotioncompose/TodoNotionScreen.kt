@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todonotioncompose

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todonotioncompose.ui.todo.TodoViewModel
import com.example.todonotioncompose.R.string
import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.model.BottomNavItem
import com.example.todonotioncompose.ui.AppViewModelProvider
import com.example.todonotioncompose.ui.auth.TokenViewModel
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.navigation.TodoNotionNavHost
import com.example.todonotioncompose.ui.search.SearchViewModel
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import com.example.todonotioncompose.utils.NavigationType

//https://stackoverflow.com/questions/72954383/jetpack-compose-bottombar-is-under-bottom-navigation
@Composable
fun TodoNotionApp(
    windowSize: WindowWidthSizeClass,
    navController: NavHostController = rememberNavController()
) {
    val todoViewModel: TodoViewModel =
        viewModel(factory = TodoViewModel.Factory)

    val userViewModel: UserViewModel =
        viewModel(factory = UserViewModel.Factory)

    val searchViewModel: SearchViewModel =
        viewModel(factory = AppViewModelProvider.Factory)

    val navigationType: NavigationType

//https://medium.com/@stefanoq21/window-size-in-jetpack-compose-696651580b55
    Scaffold(
        bottomBar = {
            if (windowSize == WindowWidthSizeClass.Compact) {
                BottomNavigationBar(
                    navController = navController,
                    userViewModel = userViewModel,
                    viewModel = todoViewModel,
                    searchViewModel = searchViewModel
                )
            }
        }
    ) { innerPadding ->
        if (windowSize > WindowWidthSizeClass.Compact) {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                MRailBar(
                    navController = navController,
                    userViewModel = userViewModel,
                    viewModel = todoViewModel,
                    searchViewModel = searchViewModel
                )
                TodoNotionNavHost(
                    navController = navController,
                    viewModel = todoViewModel,
                    userViewModel = userViewModel,
                    searchViewModel = searchViewModel
                )
            }
        } else {
            TodoNotionNavHost(
                navController = navController,
                viewModel = todoViewModel,
                userViewModel = userViewModel,
                searchViewModel = searchViewModel,
                Modifier.padding(innerPadding)
            )
        }

    }

}

/**
 * Composable that displays the topBar and displays back button if back navigation is possible.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoNotionAppBar(
    title: String,
    canNavigateBack: Boolean,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(string.back_button)
                    )
                }
                Log.d("backBtnTitle", title)

            }

        }
    )
}


//https://medium.com/@rzmeneghelo/creating-a-bottom-navigation-bar-with-jetpack-compose-a-comprehensive-guide-a5451aefc0ab
//https://developer.android.com/jetpack/compose/navigation#bottom-nav
@Composable
fun BottomNavigationBar(
    navController: NavController,
    userViewModel: UserViewModel,
    viewModel: TodoViewModel,
    searchViewModel: SearchViewModel,
    modifier: Modifier = Modifier
) {

    val items = mutableListOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.PostList,
    )
    val tokenViewModel: TokenViewModel = viewModel(factory = AppViewModelProvider.Factory)
    //check have token
    val uiState by tokenViewModel.tokensUiState.collectAsState()

    //show login and update token in userViewModel
    if (uiState.itemList.isEmpty()) {
        items.add(BottomNavItem.Login)
        items.remove(BottomNavItem.Logout)
        //init token
        userViewModel.setToken(token = Token())
    } else {
        items.remove(BottomNavItem.Login)
        items.add(BottomNavItem.Logout)
        userViewModel.setToken(uiState.itemList[0])
    }


    NavigationBar(
        modifier,
        containerColor = Color.White,
        contentColor = Color.LightGray
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        //init home
                        if (currentRoute == "home" || currentRoute == "postList") {
                            viewModel.getPhotos()
                        }

                        if (currentRoute == "search") {
                            searchViewModel.initKeyword()
                        }
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
            )
        }
    }

}


@Composable
fun MRailBar(
    navController: NavController,
    userViewModel: UserViewModel,
    viewModel: TodoViewModel,
    searchViewModel: SearchViewModel,
    modifier: Modifier = Modifier
) {

    val items = mutableListOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.PostList,
    )
    val tokenViewModel: TokenViewModel = viewModel(factory = AppViewModelProvider.Factory)
    //check have token
    val uiState by tokenViewModel.tokensUiState.collectAsState()

    //show login and update token in userViewModel
    if (uiState.itemList.isEmpty()) {
        items.add(BottomNavItem.Login)
        items.remove(BottomNavItem.Logout)
        //init token
        userViewModel.setToken(token = Token())
    } else {
        items.remove(BottomNavItem.Login)
        items.add(BottomNavItem.Logout)
        userViewModel.setToken(uiState.itemList[0])
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Log.d("clickRoute", currentRoute.toString())
    Log.d("clickRouteBollen", (currentRoute == "home").toString())

    NavigationRail {
        items.forEachIndexed { index, item ->
            NavigationRailItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                        if (currentRoute == "home" || currentRoute == "postList") {
                            viewModel.getPhotos()
                        }

                        if (currentRoute == "search") {
                            searchViewModel.initKeyword()
                        }
                    }
                }
            )
        }
    }
}


