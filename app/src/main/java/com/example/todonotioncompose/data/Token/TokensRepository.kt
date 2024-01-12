package com.example.todonotioncompose.data.Token

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [Token] from a given data source.
 */

interface TokensRepository {
    /**
     * Retrieve all the items from the the given data source.
     */
    fun getAllTokensStream(): Flow<List<Token>>

    /**
     * Retrieve an item from the given data source that matches with the [id].
     */
    fun getTokenStream(id: Int): Flow<Token?>

    /**
     * Insert item in the data source
     */
    suspend fun insertToken(token: Token)

    /**
     * Delete item from the data source
     */
    suspend fun deleteToken(token: Token)

    /**
     * Update item in the data source
     */
    suspend fun updateToken(token: Token)
}