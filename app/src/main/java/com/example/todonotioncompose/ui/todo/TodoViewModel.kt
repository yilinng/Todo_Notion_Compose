package com.example.todonotioncompose.ui.todo

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todonotioncompose.TodoApplication
import com.example.todonotioncompose.data.TodosRepository
import com.example.todonotioncompose.model.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen and Search result screen
 */
sealed interface TodoUiState {
    data class Success(val todos: List<Todo>) : TodoUiState
    object Error : TodoUiState
    object Loading : TodoUiState
}

class TodoViewModel(private val todosRepository: TodosRepository) : ViewModel()  {
    /** The mutable State that stores the status of the most recent request */
    var todoUiState: TodoUiState by mutableStateOf(TodoUiState.Loading)
        private set

    //https://stackoverflow.com/questions/68671108/jetpack-compose-mutablelivedata-not-updating-ui-components
    //https://stackoverflow.com/questions/72760708/kotlin-stateflow-not-emitting-updates-to-its-collectors
    //todo ui State
    private val _todo = MutableStateFlow(Todo())
    val todo: StateFlow<Todo> = _todo.asStateFlow()

    //filteredTodos
    /*
    private val _filteredTodos = MutableStateFlow<List<Todo>>(Todo())
    val filteredTodos: StateFlow<List<Todo>> = _filteredTodos.asStateFlow()
    */

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getPhotos()
    }

    /**
     * Gets photos information from the Mars API Retrofit service and updates the
     * [Todo] [List] [MutableList].
     */
    fun getPhotos() {
        viewModelScope.launch {
            todoUiState = TodoUiState.Loading
            todoUiState = try {
                TodoUiState.Success(todosRepository.getTodos().hits)
            } catch (e: IOException) {
                TodoUiState.Error
            } catch (e: HttpException) {
                TodoUiState.Error
            }
        }
    }

    fun getPhotosByKeyWord(searchWord: String){
        viewModelScope.launch {
            todoUiState = TodoUiState.Loading
            todoUiState = try {
                TodoUiState.Success(todosRepository.getTodosByKey(searchWord).hits)
            } catch (e: IOException) {
                TodoUiState.Error
            } catch (e: HttpException) {
                TodoUiState.Error
            }
        }
    }


    fun onTodoClick(initTodo: Todo){
        _todo.value = initTodo
        /*
        _todo.update { currentState ->
            currentState.copy(
                id = initTodo.id,
                tags = initTodo.tags,
                user = initTodo.user
            )
        }
         */
        Log.d("todoClick", todo.value.tags)
    }





    /**
     * Factory for [TodoViewModel] that takes [TodosRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TodoApplication)
                val todosRepository = application.container.todosRepository
                TodoViewModel(todosRepository = todosRepository)
            }
        }
        private const val TIMEOUT_MILLIS = 5_000L
    }
}







