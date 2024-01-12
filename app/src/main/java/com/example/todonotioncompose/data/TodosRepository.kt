package com.example.todonotioncompose.data

import com.example.todonotioncompose.model.Flower
import com.example.todonotioncompose.network.TodoApiService
import retrofit2.http.Query


/**
 * Repository that fetch photos list from pixabayApi.
 */
interface TodosRepository {
    suspend fun getTodos(): Flower

    suspend fun getTodosByKey(@Query(value="q", encoded=true) q: String): Flower
}

/**
 * Network Implementation of Repository that fetch photos list from pixabayApi.
 */
class NetworkTodosRepository(
    private val todoApiService: TodoApiService
) : TodosRepository {
    /** Fetches list of MarsPhoto from marsApi*/
    override suspend fun getTodos(): Flower = todoApiService.getPhotos()

    override suspend fun getTodosByKey(@Query(value="q", encoded=true) q: String): Flower = todoApiService.getPhotosWithKey(q)

}