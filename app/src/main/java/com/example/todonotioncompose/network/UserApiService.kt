package com.example.todonotioncompose.network

import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.model.Flower
import com.example.todonotioncompose.model.Login
import com.example.todonotioncompose.model.Post
import com.example.todonotioncompose.model.Signup
import com.example.todonotioncompose.network.dto.PostDto
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    /**
     * Returns a [List] of [Todo] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */

    //https://stackoverflow.com/questions/24100372/retrofit-and-get-using-parameters

    @POST("auth/login")
    suspend fun loginUser(@Body login: Login): Token

    @POST("auth/signup")
    suspend fun signupUser(@Body signup: Signup): ResponseBody

    //@GET("auth/user")
    //suspend fun getUser(@Header("Authorization") authorization: String): User

    @POST("auth/logout")
    suspend fun logoutUser(
        @Header("Authorization") authorization: String,
        @Body token: String
    ): ResponseBody

    @GET("todos")
    suspend fun getTodos(): List<Post>

    @GET("todos/{id}")
    suspend fun getTodo(@Path("id") postId: String): Post

    @GET("todos/search/")
    suspend fun searchTodo(@Query(value = "title", encoded = true) title: String): List<Post>

    @POST("todos")
    suspend fun addTodo(
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    @PATCH("todos/{id}")
    suspend fun editTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    @DELETE("todos/{id}")
    suspend fun deleteTodo(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String
    ): ResponseBody

}
