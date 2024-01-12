package com.example.todonotioncompose.ui.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope

import com.example.todonotioncompose.data.Keyword.Keyword
import com.example.todonotioncompose.data.Keyword.KeywordsRepository

import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.ArrayList
import java.util.Locale


class SearchViewModel(private val keywordsRepository: KeywordsRepository) : ViewModel()  {

    /**
     * Holds current item ui state
     */
    var keywordUiState by mutableStateOf(KeywordUiState())
        private set


    /**
     * Updates the [itemUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateUiState(keywordDetails: KeywordDetails) {
        keywordUiState =
            KeywordUiState(keywordDetails= keywordDetails, isEntryValid = validateInput(keywordDetails))
    }

    /**
     * Holds home ui state. The list of items are retrieved from [ KeywordsRepository] and mapped to
     * [SearchUiState]
     */
    val searchUiState: StateFlow<SearchUiState> =
        keywordsRepository.getAllKeywordsStream().map { SearchUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = SearchUiState()
            )


    /**
     * Inserts an [Keyword] in the Room database
     */
    suspend fun saveKeyword() {
        if (validateInput()) {
            keywordsRepository.insertKeyword(keywordUiState.keywordDetails.toKeyword())
        }
    }

    /**
     * Deletes the item from the [ItemsRepository]'s data source.

    suspend fun deleteKeyword() {
        keywordsRepository.deleteKeyword(keywordUiState.value.keywordDetails.toKeyword())
    }
     */
    private fun validateInput(uiState: KeywordDetails = keywordUiState.keywordDetails): Boolean {
        return with(uiState) {
            keyName.isNotBlank()
        }
    }


    fun filterByKeyWordsCount(text: String):Int{
        //creating a new array list to filter our data
        val filteredList = ArrayList<Keyword>()
        val keywordList = searchUiState.value.itemList

        // running a for loop to compare elements.
        for (item in keywordList) {
            // checking if the entered string matched with any item of our recycler view.
            if (item.keyName.lowercase().contains(text.lowercase(Locale.getDefault()))) {
                // if the item is matched we are
                // adding it to our filtered list.
                filteredList.add(item)
            }
        }
        return filteredList.size

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
data class SearchUiState(val itemList: List<Keyword> = listOf())

/**
 * Represents Ui State for an Item.
 */
data class KeywordUiState(
    val keywordDetails: KeywordDetails = KeywordDetails(),
    val isEntryValid: Boolean = false
)

data class KeywordDetails(
    val id: Long = 0,
    val keyName: String = "",
)


fun KeywordDetails.toKeyword(): Keyword = Keyword(
    id = id,
    keyName = keyName,
)

