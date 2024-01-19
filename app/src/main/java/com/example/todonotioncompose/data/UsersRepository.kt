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

    suspend fun logoutUser(
        @Header("Authorization") authorization: String,
        @Body token: String
    ): ResponseBody

    suspend fun getPosts(): List<Post>

    suspend fun getPost(@Path("id") postId: String): Post

    suspend fun searchPost(@Query(value = "title", encoded = true) title: String): List<Post>

    suspend fun addPost(
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    suspend fun editPost(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String,
        @Body postDto: PostDto
    ): Post

    suspend fun deletePost(
        @Path("id") postId: String,
        @Header("Authorization") authorization: String
    ): ResponseBody
}

/**
 * Network Implementation of Repository that fetch posts list from local api.
 */
class NetworkUsersRepository(
    private val userApiService: UserApiService
) : UsersRepository {
    /** Fetches list of MarsPhoto from marsApi*/
    override suspend fun loginUser(login: Login): Token = userApiService.loginUser(login)

    override suspend fun signupUser(signup: Signup): ResponseBody =
        userApiService.signupUser(signup)

    override suspend fun logoutUser(authorization: String, token: String): ResponseBody =
        userApiService.logoutUser(authorization, token)

    override suspend fun getPosts(): List<Post> = userApiService.getPosts()

    override suspend fun getPost(postId: String): Post = userApiService.getPost(postId)

    override suspend fun searchPost(title: String): List<Post> = userApiService.searchPost(title)

    override suspend fun addPost(authorization: String, postDto: PostDto): Post =
        userApiService.addPost(authorization, postDto)

    override suspend fun editPost(postId: String, authorization: String, postDto: PostDto): Post =
        userApiService.editPost(postId, authorization, postDto)

    override suspend fun deletePost(postId: String, authorization: String): ResponseBody =
        userApiService.deletePost(postId, authorization)


}