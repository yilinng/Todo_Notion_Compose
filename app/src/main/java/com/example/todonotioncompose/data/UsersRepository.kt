package com.example.todonotioncompose.data

import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.model.Login
import com.example.todonotioncompose.model.Post
import com.example.todonotioncompose.model.Signup

import com.example.todonotioncompose.network.UserApiService
import com.example.todonotioncompose.network.dto.PostDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * Repository that fetch data list from local api
 */
interface UsersRepository {
    suspend fun loginUser(@Body login: Login): Token

    suspend fun signupUser(@Body signup: Signup): ResponseBody

    suspend fun logoutUser(@Header("Authorization") authorization: String,
                           @Body token: String): ResponseBody

    suspend fun getTodos(): List<Post>

    suspend fun getTodo(@Path("id") postId: String): Post

    suspend fun searchTodo(@Query(value = "title", encoded = true) title: String): List<Post>

    suspend fun addTodo(
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    suspend fun editTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    suspend fun deleteTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String
    ): ResponseBody
}

/**
 * Network Implementation of Repository that fetch photos list from pixabayApi.
 */
class NetworkUsersRepository(
    private val userApiService: UserApiService
) : UsersRepository {
    /** Fetches list of MarsPhoto from marsApi*/
    override suspend fun loginUser(login: Login): Token = userApiService.loginUser(login)

    override suspend fun signupUser(signup: Signup): ResponseBody = userApiService.signupUser(signup)

    override suspend fun logoutUser(authorization: String, token: String): ResponseBody = userApiService.logoutUser(authorization, token)

    override suspend fun getTodos(): List<Post> = userApiService.getTodos()

    override suspend fun getTodo(postId: String): Post = userApiService.getTodo(postId)

    override suspend fun searchTodo(title: String): List<Post> = userApiService.searchTodo(title)

    override suspend fun addTodo(authorization: String, postDto: PostDto): Post = userApiService.addTodo(authorization, postDto)

    override suspend fun editTodo(postId: String, authorization: String, postDto: PostDto): Post = userApiService.editTodo(postId, authorization, postDto)

    override suspend fun deleteTodo(postId: String, authorization: String): ResponseBody = userApiService.deleteTodo(postId, authorization)


}