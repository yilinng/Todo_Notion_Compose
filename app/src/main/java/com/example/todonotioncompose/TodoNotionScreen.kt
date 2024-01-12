@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todonotioncompose

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationDefaults.windowInsets
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.todonotioncompose.ui.todo.TodoListScreen
import com.example.todonotioncompose.ui.todo.TodoViewModel
import com.example.todonotioncompose.R.string
import com.example.todonotioncompose.model.BottomNavItem
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.navigation.TodoNotionNavHost
import kotlinx.serialization.json.JsonNull.content

//https://stackoverflow.com/questions/72954383/jetpack-compose-bottombar-is-under-bottom-navigation
@Composable
fun TodoNotionApp(navController: NavHostController = rememberNavController()) {
    val todoViewModel: TodoViewModel =
        viewModel(factory = TodoViewModel.Factory)

    val userViewModel: UserViewModel =
        viewModel(factory = UserViewModel.Factory)
    /*
    val todoViewModel: TodoViewModel =
        viewModel(factory = TodoViewModel.Factory)
    TodoListScreen(
        todoUiState = todoViewModel.todoUiState,
        retryAction = todoViewModel::getPhotos
    )
     */
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        TodoNotionNavHost(
            navController = navController,
            viewModel = todoViewModel,
            userViewModel= userViewModel,
            Modifier.padding(innerPadding)
        )
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
            }
        }
    )
}

//https://medium.com/@rzmeneghelo/creating-a-bottom-navigation-bar-with-jetpack-compose-a-comprehensive-guide-a5451aefc0ab
//https://developer.android.com/jetpack/compose/navigation#bottom-nav
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier) {

    NavigationBar(modifier,
        containerColor=Color.White,
        contentColor=Color.LightGray) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.PostList,
            BottomNavItem.Login,
            )

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(item.label) },
            )
        }
    }


}


