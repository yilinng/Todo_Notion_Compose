package com.example.todonotioncompose.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todonotioncompose.TodoApplication
import com.example.todonotioncompose.data.TodosRepository
import com.example.todonotioncompose.data.Token.Token
import com.example.todonotioncompose.data.Token.TokensRepository
import com.example.todonotioncompose.data.UsersRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import com.example.todonotioncompose.model.*
import com.example.todonotioncompose.ui.todo.TodoViewModel
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

sealed interface LoginUiState {
    data class Success(val token: Token) : LoginUiState
    object Error : LoginUiState
    object Loading : LoginUiState
}

sealed interface SignupUiState {
    data class Success(val responseBody: ResponseBody) : SignupUiState
    object Error : SignupUiState
    object Loading : SignupUiState
}

class UserViewModel(private val usersRepository: UsersRepository) : ViewModel()  {

    /** The mutable State that stores the status of the most recent request */
    var postUiState: PostUiState by mutableStateOf(PostUiState.Loading)
        private set


    /** The mutable State that stores the status of the most recent request */
    var loginUiState: LoginUiState by mutableStateOf(LoginUiState.Loading)
        private set

    var loginInputUiState by  mutableStateOf(LoginInputUiState())
        private set

    /** The mutable State that stores the status of the most recent request */
    var signupUiState: SignupUiState by mutableStateOf(SignupUiState.Loading)
        private set


    var signupInputUiState by  mutableStateOf(SignupInputUiState())
        private set

    //todo ui State
    private val _post = MutableStateFlow(Post())
    val post: StateFlow<Post> = _post.asStateFlow()


    init {
        getPostsAction()
    }


    //https://stackoverflow.com/questions/33815515/how-do-i-get-response-body-when-there-is-an-error-when-using-retrofit-2-0-observ
    private fun loginAction(login: Login) {
        viewModelScope.launch {
            loginUiState = LoginUiState.Loading
            loginUiState = try {
                LoginUiState.Success(usersRepository.loginUser(login))
            } catch (e: IOException) {
                LoginUiState.Error
            } catch (e: HttpException) {
                LoginUiState.Error
            }
        }
    }

    fun signupAction(signup: Signup) {
        viewModelScope.launch {
            signupUiState = SignupUiState.Loading
            signupUiState = try {
                SignupUiState.Success(usersRepository.signupUser(signup))
            } catch (e: IOException) {
                SignupUiState.Error
            } catch (e: HttpException) {
                SignupUiState.Error
            }
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
                PostUiState.Success(usersRepository.getTodos())
            } catch (e: IOException) {
                PostUiState.Error
            } catch (e: HttpException) {
                PostUiState.Error
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

    fun checkLogin(loginDetails: LoginDetails){
        if(validateLoginInput()){
            loginAction(loginDetails.toLogin())
        }
    }

    private fun validateSignupInput(uiState: SignupDetails= signupInputUiState.signupDetails): Boolean {
        return with(uiState) {
          name.isNotBlank()  && username.isNotBlank() && email.isNotBlank() && password.isNotBlank()
        }
    }

    fun checkSignup(signupDetails: SignupDetails){
        if(validateSignupInput()){
            signupAction(signupDetails.toSignup())
        }
    }

    fun onPostClick(initPost: Post) {
        _post.value = initPost
    }


    /**
     * Factory for [TodoViewModel] that takes [TodosRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
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
    val usernameOrEmail: String = "",
    val password: String = "",
)

data class SignupDetails(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val password: String = "",
)

data class SignupInputUiState(
    val signupDetails: SignupDetails = SignupDetails(),
    val isEntryValid: Boolean = false
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
 * Extension function to convert [ItemUiState] to [Item]. If the value of [ItemDetails.price] is
 * not a valid [Double], then the price will be set to 0.0. Similarly if the value of
 * [ItemUiState] is not a valid [Int], then the quantity will be set to 0
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
    name= name,
    username = username,
    email = email,
    password = password
)