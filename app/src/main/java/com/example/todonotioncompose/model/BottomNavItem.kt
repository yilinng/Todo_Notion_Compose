package com.example.todonotioncompose.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home

import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector


sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem("home", Icons.Default.Home, "Home")
    object Search : BottomNavItem("search", Icons.Default.Search, "Search")
    object PostList : BottomNavItem("postList", Icons.Default.Folder, "PostList")
    object Login : BottomNavItem("login",Icons.Default.AccountCircle, "Login")

}

