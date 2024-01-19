package com.example.todonotioncompose.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todonotioncompose.TodoApplication
import com.example.todonotioncompose.ui.search.SearchViewModel

import com.example.todonotioncompose.ui.auth.TokenViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for SearchViewModel
        initializer {
            SearchViewModel(todoApplication().container.keywordsRepository)
        }

        // Initializer for TokenViewModel
        initializer {
            TokenViewModel(todoApplication().container.tokensRepository)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [TodoApplication].
 */
fun CreationExtras.todoApplication(): TodoApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)