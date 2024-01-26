package com.example.todonotioncompose.fake

import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.model.JwtAuthResponse
import com.example.todonotioncompose.model.Login
import com.example.todonotioncompose.model.Post
import com.example.todonotioncompose.model.Signup
import okhttp3.Response
import okhttp3.ResponseBody

object FakeDataSource {
    const val idOne = "id1"
    const val idTwo = "id2"
    const val idThree = "id3"
    const val titleOne = "title1"
    const val titleTwo = "title2"
    const val titleThree = "title3"
    const val contentOne = "content1"
    const val contentTwo = "content22"
    const val contentThree = "content3"
    const val contentThreeUpdate = "content33"

    const val createDateOne = "createDate1"
    const val createDateTwo = "createDate2"
    const val createDateThree = "createDate3"
    const val updateDateOne = "updateDate2"
    const val updateDateTwo = "updateDate2"
    const val updateDateThree = "updateDate3"

    const val userIdOne = "userId1"
    const val userIdTwo = "userId2"


    val postsList = listOf(
        Post(
            id = idOne,
            title = titleOne,
            content = contentOne,
            createDate = createDateOne,
            updateDate = updateDateOne,
            userId = userIdOne
        ),
        Post(
            id = idTwo,
            title = titleTwo,
            content = contentTwo,
            createDate = createDateTwo,
            updateDate = updateDateTwo,
            userId = userIdTwo
        )
    )

    val addPost = Post(
        id = idThree,
        title = titleThree,
        content = contentThree,
        createDate = createDateThree,
        updateDate = updateDateThree,
        userId = userIdOne
    )

    val editPost =  Post(
        id = idThree,
        title = titleThree,
        content = contentThreeUpdate,
        createDate = createDateThree,
        updateDate = updateDateThree,
        userId = userIdOne
    )

    const val idToken = 1233467788222222
    const val accessToken ="accessToken"
    const val refreshToken ="refreshToken"

    val fakeToken = Token(id= idToken, accessToken = accessToken, refreshToken = refreshToken, userId= userIdOne)

    val fakeAuthorization = "fakeAuthorization"

    const val testUsername ="testUser"
    const val testEmail ="testemail@email.com"
    const val testName ="test name"
    const val testPassword ="testPassword"

    val fakeSignup = Signup(name = testName, username = testUsername, email = testEmail, password = testPassword)

    val fakeLogin = Login(usernameOrEmail = testUsername, password = testPassword)

    val signupResponse = JwtAuthResponse(message = "User registered successfully!.")

    // const val signupMessage = "User registered successfully!."
   // val responseBodySignup = Response()



}
