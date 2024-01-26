package com.example.todonotioncompose.ui.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todonotioncompose.TodoApplication
import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.data.Token.TokensRepository
import com.example.todonotioncompose.data.UsersRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.example.todonotioncompose.model.*
import com.example.todonotioncompose.network.dto.PostDto

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.ResponseBody
import java.io.IOException

/**
 * UI state for the Post list screen
 */
sealed interface PostUiState {
    data class Success(val posts: List<Post>) : PostUiState
    object Error : PostUiState
    object Loading : PostUiState
}

sealed interface SinglePostUiState {
    data class Success(val post: Post) : SinglePostUiState
    object Error : SinglePostUiState
    object Loading : SinglePostUiState
    object Default : SinglePostUiState
}

sealed interface ResponsePostUiState {
    data class Success(val responseBody: ResponseBody) : ResponsePostUiState
    object Error : ResponsePostUiState
    object Loading : ResponsePostUiState
    object Default : ResponsePostUiState
}

sealed interface LoginUiState {
    data class Success(val token: Token) : LoginUiState
    data class Error(val errorText: String) : LoginUiState
    object Loading : LoginUiState
    object Default : LoginUiState

}

sealed interface SignupUiState {
    data class Success(val jwtAuthResponse: JwtAuthResponse) : SignupUiState
    data class Error(val errorText: String) : SignupUiState
    object Loading : SignupUiState
    object Default : SignupUiState

}

class UserViewModel(private val usersRepository: UsersRepository) : ViewModel() {

    /** The mutable State that stores the status of the most recent request */
    var postUiState: PostUiState by mutableStateOf(PostUiState.Loading)
        private set

    /** The mutable State that stores the status of the most recent request */
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Default)
        private set

    var loginInputUiState by mutableStateOf(LoginInputUiState())
        private set

    /** The mutable State that stores the status of the most recent request */
    var signupUiState: SignupUiState by mutableStateOf(SignupUiState.Default)
        private set

    var signupInputUiState by mutableStateOf(SignupInputUiState())
        private set

    var singlePostUiState: SinglePostUiState by mutableStateOf(SinglePostUiState.Default)
        private set

    var singlePostInputUiState by mutableStateOf(SinglePostInputUiState())
        private set

    var responsePostUiState: ResponsePostUiState by mutableStateOf(ResponsePostUiState.Default)
        private set


    //https://stackoverflow.com/questions/68671108/jetpack-compose-mutablelivedata-not-updating-ui-components
    //https://stackoverflow.com/questions/72760708/kotlin-stateflow-not-emitting-updates-to-its-collectors
    //post ui State
    private val _post = MutableStateFlow(Post())
    val post: StateFlow<Post> = _post.asStateFlow()

    private val _token = MutableStateFlow<Token?>(Token())
    val token: StateFlow<Token?> = _token.asStateFlow()


    init {
        getPostsAction()
    }


    /**
     * Updates the [LoginUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateLoginUiState(loginDetails: LoginDetails) {
        loginInputUiState =
            LoginInputUiState(
                loginDetails = loginDetails,
                isEntryValid = validateLoginInput(loginDetails)
            )
    }


    /**
     * Updates the [SignupUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updateSignupUiState(signupDetails: SignupDetails) {
        signupInputUiState =
            SignupInputUiState(
                signupDetails = signupDetails,
                isEntryValid = validateSignupInput(signupDetails)
            )
    }

    /**
     * Updates the [SinglePostInputUiState] with the value provided in the argument. This method also triggers
     * a validation for input values.
     */
    fun updatePostInputUiState(postDetails: PostDetails) {
        singlePostInputUiState =
            SinglePostInputUiState(
                postDetails = postDetails,
                isEntryValid = validatePostInput(postDetails)
            )
    }


    //https://stackoverflow.com/questions/33815515/how-do-i-get-response-body-when-there-is-an-error-when-using-retrofit-2-0-observ
    private fun loginAction(login: Login) {
        //  Log.d("login", login.toString())
        viewModelScope.launch {
            loginUiState = LoginUiState.Loading
            loginUiState = try {
                LoginUiState.Success(usersRepository.loginUser(login))
            } catch (e: HttpException) {
                LoginUiState.Error(e.response()?.errorBody()!!.string())
            }

            // Log.d("loginStateAction", loginUiState.toString())

        }
    }

    private fun signupAction(signup: Signup) {
        viewModelScope.launch {
            signupUiState = SignupUiState.Loading
            signupUiState = try {
                SignupUiState.Success(usersRepository.signupUser(signup))
            } catch (e: HttpException) {
                SignupUiState.Error(e.response()?.errorBody()!!.string())
            }
            /*
            Log.d("signupStateAction1", signupUiState.toString())

            Log.d("signupStateAction2", signupUiState.toString().contains("Username").toString())
            Log.d("signupStateAction3", signupUiState.toString().contains("Email").toString())
            */
        }
    }

    /*
    fun getUserAction(token: String ) {
        viewModelScope.launch {
            _status.value = UserApiStatus.LOADING
            val accessToken = getToken()
            Log.d("getUser accessToken", "Bearer $accessToken")
            //Log.d("getUser accessToken", "Bearer $token")
            try {
                _user.value = UserApi.retrofitService.getUser(authorization= "Bearer $token")
                _status.value = UserApiStatus.DONE
                Log.d("getUser200",  user.toString())

                _filteredPosts.value = user.value!!.todos
            } catch (e: Exception) {
                _status.value = UserApiStatus.ERROR
                //_posts.value = listOf()
                Log.e("getUser404", e.toString())

            }
        }
    }
    */
    fun getPostsAction() {
        viewModelScope.launch {
            postUiState = PostUiState.Loading
            postUiState = try {
                PostUiState.Success(usersRepository.getPosts())
            } catch (e: IOException) {
                PostUiState.Error
            } catch (e: HttpException) {
                PostUiState.Error
            }
        }

    }

    private fun addPostAction(postDto: PostDto) {
        val accessToken = getToken()
        viewModelScope.launch {
            singlePostUiState = SinglePostUiState.Loading
            //   Log.d("addPostAction accessToken", "Bearer $accessToken")
            singlePostUiState = try {
                SinglePostUiState.Success(
                    usersRepository.addPost(
                        authorization = "Bearer $accessToken",
                        postDto
                    )
                )
            } catch (e: Exception) {
                SinglePostUiState.Error
            }
            //   Log.d("addPostAction_to_string", singlePostUiState.toString())

            if (singlePostUiState.toString().contains("Success")) {
                getPostsAction()
            }

        }
    }

    private fun editPostAction(postId: String, postDto: PostDto) {
        viewModelScope.launch {
            singlePostUiState = SinglePostUiState.Loading
            val accessToken = getToken()
            //  Log.d("editPostAction accessToken", "Bearer $accessToken")
            singlePostUiState = try {
                SinglePostUiState.Success(
                    usersRepository.editPost(
                        postId = postId,
                        authorization = "Bearer $accessToken",
                        postDto
                    )
                )
            } catch (e: Exception) {
                SinglePostUiState.Error
            }

            if (singlePostUiState.toString().contains("Success")) {
                getPostsAction()
            }
        }
    }

    fun deletePostAction(postId: String) {
        viewModelScope.launch {
            responsePostUiState = ResponsePostUiState.Loading
            val accessToken = getToken()
            Log.d("deletePostAction accessToken", "Bearer $accessToken")
            responsePostUiState = try {
                ResponsePostUiState.Success(
                    usersRepository.deletePost(
                        postId = postId,
                        authorization = "Bearer $accessToken"
                    )
                )
            } catch (e: Exception) {
                ResponsePostUiState.Error
            }

            if (responsePostUiState.toString().contains("Success")) {
                getPostsAction()
            }
        }
    }

    /**
     * Deletes the item from the [TokensRepository]'s data source.

    suspend fun deleteKeyword() {
    keywordsRepository.deleteKeyword(keywordUiState.value.keywordDetails.toKeyword())
    }
     */
    private fun validateLoginInput(uiState: LoginDetails = loginInputUiState.loginDetails): Boolean {
        return with(uiState) {
            usernameOrEmail.isNotBlank() && password.isNotBlank()
        }
    }

    fun checkLogin(loginDetails: LoginDetails) {
        // Log.d("checkLogin", loginDetails.toString())
        if (validateLoginInput()) {
            loginAction(loginDetails.toLogin())
        }
    }

    private fun validateSignupInput(uiState: SignupDetails = signupInputUiState.signupDetails): Boolean {
        return with(uiState) {
            name.isNotBlank() && username.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        }
    }

    fun checkAddPost(postDetails: PostDetails) {
        //Log.d("checkAddPost", postDetails.toString())
        if (validatePostInput()) {
            addPostAction(postDetails.toPost())
        }
    }

    fun checkEditPost(postId: String, postDetails: PostDetails) {
        //  Log.d("checkEditPost", postDetails.toString())
        if (validatePostInput()) {
            editPostAction(postId, postDetails.toPost())
        }
    }

    private fun validatePostInput(uiState: PostDetails = singlePostInputUiState.postDetails): Boolean {
        return with(uiState) {
            title.isNotBlank() && content.isNotBlank()
        }
    }

    fun checkSignup(signupDetails: SignupDetails) {
        if (validateSignupInput()) {
            signupAction(signupDetails.toSignup())
        }
    }

    fun initLogin() {
        loginInputUiState.loginDetails.usernameOrEmail = ""
        loginInputUiState.loginDetails.password = ""
    }

    fun initSignup() {
        signupInputUiState.signupDetails.name = ""
        signupInputUiState.signupDetails.username = ""
        signupInputUiState.signupDetails.email = ""
        signupInputUiState.signupDetails.password = ""
    }

    fun initPost() {
        singlePostInputUiState.postDetails.title = ""
        singlePostInputUiState.postDetails.content = ""
    }

    fun initLoginUiState() {
        loginUiState = LoginUiState.Default
    }

    fun initSignupUiState() {
        signupUiState = SignupUiState.Default
    }

    fun initToken() {
        _token.value = null
    }

    fun initSignPostUiState() {
        singlePostUiState = SinglePostUiState.Default
    }


    fun setToken(token: Token) {
        _token.value = token
    }

    private fun getToken(): String {
        return if (token.value != null) {
            token.value!!.accessToken
        } else {
            ""
        }
    }

    fun slicePostTitle(str: String): String {
        if (str.length > 50) {
            return str.substring(0, 50) + "..."
        } else {
            return str
        }
    }

    fun slicePostContent(str: String): String {
        if (str.length > 80) {
            return str.substring(0, 80) + "..."
        } else {
            return str
        }
    }


    fun onPostClick(initPost: Post) {
        _post.value = initPost
    }


    /**
     * Factory for [UserViewModel] that takes [UsersRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as TodoApplication)
                val usersRepository = application.container.usersRepository
                UserViewModel(usersRepository = usersRepository)
            }
        }
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

/**
 * Represents Ui State for an Login.
 */
data class LoginInputUiState(
    val loginDetails: LoginDetails = LoginDetails(),
    val isEntryValid: Boolean = false
)

data class LoginDetails(
    var usernameOrEmail: String = "",
    var password: String = "",
)

data class SignupDetails(
    var name: String = "",
    var username: String = "",
    var email: String = "",
    var password: String = "",
)

data class SignupInputUiState(
    val signupDetails: SignupDetails = SignupDetails(),
    val isEntryValid: Boolean = false
)

data class SinglePostInputUiState(
    val postDetails: PostDetails = PostDetails(),
    val isEntryValid: Boolean = false
)

data class PostDetails(
    var title: String = "",
    var content: String = "",
)

/**
 * Extension function to convert [ItemUiState] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun SignupDetails.toSignup(): Signup = Signup(
    name = name,
    username = username,
    email = email,
    password = password
)


/**
 * Extension function to convert [PostUiState] to [Item]. If the value of [PostDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [SinglePostUiState] is not a valid [Int], then the quantity will be set to 0
 */
fun LoginDetails.toLogin(): Login = Login(
    usernameOrEmail = usernameOrEmail,
    password = password
)

/**
 * Extension function to convert [Login] to [LoginUiState]
 */
fun Login.toLoginUiState(isEntryValid: Boolean = false): LoginInputUiState = LoginInputUiState(
    loginDetails = this.toLogin(),
    isEntryValid = isEntryValid
)

fun PostDetails.toPost(): PostDto = PostDto(
    title = title,
    content = content
)


/**
 * Extension function to convert [Login] to [LoginDetails]
 */
fun Login.toLogin(): LoginDetails = LoginDetails(
    usernameOrEmail = usernameOrEmail,
    password = password
)

/**
 * Extension function to convert [Login] to [LoginDetails]
 */
fun Signup.toSignup(): SignupDetails = SignupDetails(
    name = name,
    username = username,
    email = email,
    password = password
)

/**
 * Extension function to convert [Post] to [PostDetails]
 */
fun Post.toPost(): PostDetails = PostDetails(
    title = title,
    content = content
)