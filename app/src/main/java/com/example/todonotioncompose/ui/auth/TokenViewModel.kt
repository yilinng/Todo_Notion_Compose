package com.example.todonotioncompose.ui.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todonotioncompose.data.Keyword.Keyword
import com.example.todonotioncompose.data.Keyword.KeywordsRepository
import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.data.Token.TokensRepository
import com.example.todonotioncompose.model.Todo
import com.example.todonotioncompose.ui.search.SearchViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class TokenViewModel(private val tokensRepository: TokensRepository) : ViewModel() {
    /**
     * Holds current item ui state
     */
    var tokenUiState by mutableStateOf(TokenUiState())
        private set


    /**
     * Updates the [tokenUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(tokenDetails: TokenDetails) {
        tokenUiState =
            TokenUiState(tokenDetails = tokenDetails, isEntryValid = validateInput(tokenDetails))
    }

    /**
     * Holds home ui state. The list of items are retrieved from [ KeywordsRepository] and mapped to
     * [TokenUiState]
     */
    val tokensUiState: StateFlow<TokensUiState> =
        tokensRepository.getAllTokensStream()
            .map { TokensUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TokensUiState()
            )


    /**
     * Inserts an [Keyword] in the Room database
     */
    suspend fun saveToken() {
        if (validateInput()) {
            tokensRepository.insertToken(tokenUiState.tokenDetails.toToken())
        }
    }

    /**
     * Deletes the token from the [TokensRepository]'s data source.
     */
    suspend fun deleteToken() {
        Log.d("logoutAction", tokensUiState.value.itemList[0].toString())
        tokensRepository.deleteToken(tokensUiState.value.itemList[0])
    }

    private fun validateInput(uiState: TokenDetails = tokenUiState.tokenDetails): Boolean {
        return with(uiState) {
            accessToken.isNotBlank() && refreshToken.isNotBlank() && userId.isNotBlank()
        }
    }


    /**
     * Factory for [SearchViewModel] that takes [KeywordsRepository] as a dependency
     */
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

/**
 * Ui State for TodoList
 */
data class TokensUiState(val itemList: List<Token> = listOf())

/**
 * Represents Ui State for an Item.
 */
data class TokenUiState(
    val tokenDetails: TokenDetails = TokenDetails(),
    val isEntryValid: Boolean = false
)

data class TokenDetails(
    val id: Long = 0,
    val accessToken: String = "",
    val refreshToken: String = "",
    val userId: String = ""
)


fun TokenDetails.toToken(): Token = Token(
    id = id,
    accessToken = accessToken,
    refreshToken = refreshToken,
    userId = userId
)

/**
 * Extension function to convert [Token] to [TokenDetails]
 */

fun Token.toToken(): TokenDetails = TokenDetails(
    id = id,
    refreshToken = refreshToken,
    accessToken = accessToken,
    userId = userId
)
