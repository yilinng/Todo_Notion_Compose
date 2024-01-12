package com.example.todonotioncompose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.todonotioncompose.model.BottomNavItem
import com.example.todonotioncompose.ui.auth.LoginScreen
import com.example.todonotioncompose.ui.auth.LoginScreenDestination
import com.example.todonotioncompose.ui.auth.SignupScreen
import com.example.todonotioncompose.ui.auth.SignupScreenDestination
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.post.PostDetailsScreen
import com.example.todonotioncompose.ui.post.PostDetailsScreenDestination
import com.example.todonotioncompose.ui.post.PostListScreen
import com.example.todonotioncompose.ui.search.SearchResultScreen
import com.example.todonotioncompose.ui.search.SearchResultScreenDestination
import com.example.todonotioncompose.ui.search.SearchScreen
import com.example.todonotioncompose.ui.search.SearchScreenDestination
import com.example.todonotioncompose.ui.todo.HomeDestination
import com.example.todonotioncompose.ui.todo.TodoDetailScreen
import com.example.todonotioncompose.ui.todo.TodoDetailScreenDestination
import com.example.todonotioncompose.ui.todo.TodoListScreen
import com.example.todonotioncompose.ui.todo.TodoViewModel

/**
 * Provides Navigation graph for the application.
 * https://discuss.kotlinlang.org/t/is-it-possible-to-call-a-function-by-reflection-inside-other-function/6677/2
 */
@Composable
fun TodoNotionNavHost(
    navController: NavHostController,
    viewModel: TodoViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = BottomNavItem.Home.route) {

            TodoListScreen(
                navigateToTodoDetails = {
                    navController.navigate("${TodoDetailScreenDestination.route}/${it}")
                },
                todoUiState = viewModel.todoUiState,
                retryAction = viewModel::getPhotos,
                viewModel = viewModel
            )

        }
        composable(
            route = TodoDetailScreenDestination.routeWithArgs,
            /*
            arguments = listOf(navArgument(TodoDetailScreenDestination.todoIdArg) {
                type = NavType.IntType
            })
             */
        ) {
            TodoDetailScreen(
                navigateBack = { navController.navigateUp() },
                viewModel = viewModel,
            )
        }

        composable(
            route = BottomNavItem.Search.route
        ) {
            SearchScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToSearchResult = {
                    navController.navigate(SearchResultScreenDestination.route)
                },
                viewModel = viewModel,
                )
        }

        composable(
            route = SearchResultScreenDestination.route
        ) {
            SearchResultScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToTodoDetails = {
                    navController.navigate("${TodoDetailScreenDestination.route}/${it}")
                },
                todoUiState = viewModel.todoUiState,
                retryAction = { viewModel.getPhotosByKeyWord(TodoDetailScreenDestination.todoIdArg) },
                viewModel = viewModel
            )
        }

        composable(
            route = LoginScreenDestination.route
        ) {
            LoginScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToHome = {
                    navController.navigate(BottomNavItem.Home.route)
                },
                navigateToSignup = {
                    navController.navigate(SignupScreenDestination.route)
                },
                userViewModel = userViewModel,
                loginUiState = userViewModel.loginUiState,
                )
        }

        composable(
            route = SignupScreenDestination.route
        ) {
            SignupScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                navigateToHome = {
                    navController.navigate(BottomNavItem.Home.route)
                },
                navigateToLogin = {
                    navController.navigate(LoginScreenDestination.route)
                },
                userViewModel = userViewModel
            )
        }

        composable(route = BottomNavItem.PostList.route) {

            PostListScreen(
                navigateToPostDetails = {
                    navController.navigate("${PostDetailsScreenDestination.route}/${it}")
                },
                postUiState = userViewModel.postUiState,
                retryAction = userViewModel::getPostsAction,
                userViewModel = userViewModel,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }

            )

        }
        composable(
            route = PostDetailsScreenDestination.routeWithArgs,
            /*
            arguments = listOf(navArgument(TodoDetailScreenDestination.todoIdArg) {
                type = NavType.IntType
            })
             */
        ) {
            PostDetailsScreen(
                navigateBack = { navController.navigateUp() },
                postUiState = userViewModel.postUiState,
                userViewModel = userViewModel,
                retryAction = userViewModel::getPostsAction,
            )
        }

    }
}