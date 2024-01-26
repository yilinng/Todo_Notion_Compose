package com.example.todonotioncompose.fake

import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.model.JwtAuthResponse
import com.example.todonotioncompose.model.Login
import com.example.todonotioncompose.model.Post
import com.example.todonotioncompose.model.Signup
import com.example.todonotioncompose.network.UserApiService
import com.example.todonotioncompose.network.dto.PostDto
import okhttp3.ResponseBody

class FakeUsersApiService: UserApiService {
    override suspend fun loginUser(login: Login): Token {
        return FakeDataSource.fakeToken
    }

    override suspend fun signupUser(signup: Signup): JwtAuthResponse {
        return FakeDataSource.signupResponse
    }

    override suspend fun logoutUser(authorization: String, token: String): ResponseBody {
        TODO("Not yet implemented")
    }

    override suspend fun getPosts(): List<Post> {
        return FakeDataSource.postsList
    }

    override suspend fun getPost(postId: String): Post {
        return FakeDataSource.postsList[0]
    }

    override suspend fun searchPost(title: String): List<Post> {
        return FakeDataSource.postsList
    }

    override suspend fun addPost(authorization: String, postDto: PostDto): Post {
        return FakeDataSource.addPost
    }

    override suspend fun editPost(postId: String, authorization: String, postDto: PostDto): Post {
        return FakeDataSource.editPost
    }

    override suspend fun deletePost(postId: String, authorization: String): ResponseBody {
        TODO("Not yet implemented")
    }
}