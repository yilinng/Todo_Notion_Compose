package com.example.todonotioncompose.fake

import com.example.todonotioncompose.data.NetworkUsersRepository
import com.example.todonotioncompose.fake.FakeDataSource.fakeLogin
import com.example.todonotioncompose.fake.FakeDataSource.fakeSignup
import com.example.todonotioncompose.fake.FakeDataSource.idOne
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NetworkUsersRepositoryTest {

    @Test
    fun networkUsersRepository_getPosts_verifyPostList() =
        runTest {
            val repository = NetworkUsersRepository(
                userApiService = FakeUsersApiService()
            )
            assertEquals(FakeDataSource.postsList, repository.getPosts())
        }

    /*
    @Test
    fun networkUsersRepository_getPost_verifyPost() =
        runTest {
            val repository = NetworkUsersRepository(
                userApiService = FakeUsersApiService()
            )
            assertEquals(FakeDataSource.postsList[0], repository.getPost(idOne))
        }
    */
    @Test
    fun networkUsersRepository_login_verifyToken() =
        runTest {
            val repository = NetworkUsersRepository(
                userApiService = FakeUsersApiService()
            )
            assertEquals(FakeDataSource.fakeToken, repository.loginUser(fakeLogin))
        }

    @Test
    fun networkUsersRepository_signup_verifyToken() =
        runTest {
            val repository = NetworkUsersRepository(
                userApiService = FakeUsersApiService()
            )
            assertEquals(FakeDataSource.signupResponse, repository.signupUser(fakeSignup))
        }
}