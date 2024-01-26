package com.example.todonotioncompose.ui.test

import com.example.todonotioncompose.fake.FakeDataSource
import com.example.todonotioncompose.fake.FakeUserRepository
import com.example.todonotioncompose.rules.TestDispatcherRule
import com.example.todonotioncompose.ui.auth.*

import org.junit.Assert.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


/*
* https://developer.android.com/codelabs/basic-android-kotlin-compose-add-repository?
* continue=https%3A%2F%2Fdeveloper.android.com%2Fcourses%2Fpathways%2Fandroid-basics-compose-unit-5-pathway-2%23codelab-https%3A%2F%2Fdeveloper.android.com%2Fcodelabs%2Fbasic-android-kotlin-compose-add-repository#6
* */
//https://developer.android.com/codelabs/advanced-android-kotlin-training-testing-basics#0
class UserViewModelTest {
    @get:Rule
    val testDispatcher = TestDispatcherRule()

    @Test
    fun userViewModel_getPosts_verifyPostUiStateSuccess() =
        runTest{
            val userViewModel = UserViewModel(
                usersRepository = FakeUserRepository()
            )

            assertEquals(
                PostUiState.Success(FakeDataSource.postsList),
                userViewModel.postUiState
            )
        }

    @Test
    fun userViewModel_addPost_verifySinglePostUiStateSuccess() =
        runTest{
            val userViewModel = UserViewModel(
                usersRepository = FakeUserRepository()
            )

            userViewModel.singlePostInputUiState.postDetails.title = FakeDataSource.addPost.title
            userViewModel.singlePostInputUiState.postDetails.content = FakeDataSource.addPost.content

            userViewModel.checkAddPost(FakeDataSource.addPost.toPost())

            assertEquals(
                SinglePostUiState.Success(FakeDataSource.addPost),
                userViewModel.singlePostUiState
            )
        }


    @Test
    fun userViewModel_editPost_verifySinglePostUiStateSuccess() =
        runTest{
            val userViewModel = UserViewModel(
                usersRepository = FakeUserRepository()
            )

            userViewModel.singlePostInputUiState.postDetails.title = FakeDataSource.editPost.title
            userViewModel.singlePostInputUiState.postDetails.content = FakeDataSource.editPost.content

            userViewModel.checkEditPost(FakeDataSource.idThree, FakeDataSource.editPost.toPost())

            assertEquals(
                SinglePostUiState.Success(FakeDataSource.editPost),
                userViewModel.singlePostUiState
            )
        }

    @Test
    fun userViewModel_loginUser_verifyLoginUiStateSuccess() =
        runTest{
            val userViewModel = UserViewModel(
                usersRepository = FakeUserRepository()
            )

            userViewModel.loginInputUiState.loginDetails.usernameOrEmail = FakeDataSource.fakeLogin.usernameOrEmail
            userViewModel.loginInputUiState.loginDetails.password = FakeDataSource.fakeLogin.password


            userViewModel.checkLogin(FakeDataSource.fakeLogin.toLogin())

            assertEquals(
                LoginUiState.Success(FakeDataSource.fakeToken),
                userViewModel.loginUiState
            )
        }

    @Test
    fun userViewModel_signupUser_verifySignupUiStateSuccess() =
        runTest{
            val userViewModel = UserViewModel(
                usersRepository = FakeUserRepository()
            )

            userViewModel.signupInputUiState.signupDetails.name = FakeDataSource.fakeSignup.name
            userViewModel.signupInputUiState.signupDetails.username = FakeDataSource.fakeSignup.username
            userViewModel.signupInputUiState.signupDetails.email = FakeDataSource.fakeSignup.email
            userViewModel.signupInputUiState.signupDetails.password = FakeDataSource.fakeSignup.password


            userViewModel.checkSignup(FakeDataSource.fakeSignup.toSignup())

            assertEquals(
                SignupUiState.Success(FakeDataSource.signupResponse),
                userViewModel.signupUiState
            )
        }
}