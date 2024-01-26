package com.example.todonotioncompose.network

import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.model.JwtAuthResponse
import com.example.todonotioncompose.model.Login
import com.example.todonotioncompose.model.Post
import com.example.todonotioncompose.model.Signup
import com.example.todonotioncompose.network.dto.PostDto
import okhttp3.ResponseBody
import retrofit2.http.*


interface UserApiService {
    /**
     * Returns a [List] of [Post] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */

    //https://stackoverflow.com/questions/24100372/retrofit-and-get-using-parameters

    @POST("auth/login")
    suspend fun loginUser(@Body login: Login): Token

    @POST("auth/signup")
    suspend fun signupUser(@Body signup: Signup): JwtAuthResponse

    //@GET("auth/user")
    //suspend fun getUser(@Header("Authorization") authorization: String): User

    @POST("auth/logout")
    suspend fun logoutUser(
        @Header("Authorization") authorization: String,
        @Body token: String
    ): ResponseBody

    @GET("todos")
    suspend fun getPosts(): List<Post>

    @GET("todos/{id}")
    suspend fun getPost(@Path("id") postId: String): Post

    @GET("todos/search/")
    suspend fun searchPost(@Query(value = "title", encoded = true) title: String): List<Post>

    @POST("todos")
    suspend fun addPost(
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    @PATCH("todos/{id}")
    suspend fun editPost(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    @DELETE("todos/{id}")
    suspend fun deletePost(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String
    ): ResponseBody

}
