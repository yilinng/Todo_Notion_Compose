package com.example.todonotioncompose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.navArgument
import com.example.todonotioncompose.R
import com.example.todonotioncompose.model.BottomNavItem
import com.example.todonotioncompose.ui.auth.LoginScreen
import com.example.todonotioncompose.ui.auth.LoginScreenDestination
import com.example.todonotioncompose.ui.auth.LogoutDialog
import com.example.todonotioncompose.ui.auth.LogoutDialogDestination
import com.example.todonotioncompose.ui.auth.SignupScreen
import com.example.todonotioncompose.ui.auth.SignupScreenDestination
import com.example.todonotioncompose.ui.auth.UserViewModel
import com.example.todonotioncompose.ui.post.PostDetailsScreen
import com.example.todonotioncompose.ui.post.PostDetailsScreenDestination
import com.example.todonotioncompose.ui.post.PostEditDestination
import com.example.todonotioncompose.ui.post.PostEditScreen
import com.example.todonotioncompose.ui.post.PostEntryDestination
import com.example.todonotioncompose.ui.post.PostEntryScreen
import com.example.todonotioncompose.ui.post.PostListScreen
import com.example.todonotioncompose.ui.search.SearchResultScreen
import com.example.todonotioncompose.ui.search.SearchResultScreenDestination
import com.example.todonotioncompose.ui.search.SearchScreen
import com.example.todonotioncompose.ui.search.SearchScreenDestination
import com.example.todonotioncompose.ui.search.SearchViewModel
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
    searchViewModel: SearchViewModel,
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
                searchViewModel = searchViewModel
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
                viewModel = viewModel,
                searchViewModel = searchViewModel
            )
        }

        composable(
            route = BottomNavItem.Login.route
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
                userViewModel = userViewModel,
                signupUiState = userViewModel.signupUiState,
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
                onNavigateUp = { navController.navigateUp() },
                navigateToPostEntry = {
                    navController.navigate(PostEntryDestination.route)
                }
            )
        }
        composable(
            route = PostDetailsScreenDestination.routeWithArgs
        ) {
            PostDetailsScreen(
                navigateBack = { navController.navigateUp() },
                postUiState = userViewModel.postUiState,
                userViewModel = userViewModel,
                retryAction = userViewModel::getPostsAction,
                navigateToPostEdit = {
                    navController.navigate("${PostEditDestination.route}/${it}")
                }
            )
        }

        composable(route = PostEntryDestination.route) {

            PostEntryScreen(
                userViewModel = userViewModel,
                navigateBack = { navController.popBackStack()},
                onNavigateUp = { navController.navigateUp()},
                singlePostUiState = userViewModel.singlePostUiState
            )

        }


        composable(
            route = PostEditDestination.routeWithArgs
        ) {

            PostEditScreen(
                userViewModel = userViewModel,
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                singlePostUiState = userViewModel.singlePostUiState
            )

        }

        dialog(
            route = LogoutDialogDestination.route,
            dialogProperties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            )
        ) {
            LogoutDialog(
                userViewModel = userViewModel,
                navigateBack = { navController.popBackStack()},
                navigateToLogin = {
                    navController.navigate(BottomNavItem.Login.route)
                },
            )
        }

    }
}