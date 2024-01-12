package com.example.todonotioncompose.data

import com.example.todonotioncompose.data.Keyword.KeywordsRepository
import com.example.todonotioncompose.data.Keyword.OfflineKeywordsRepository
import com.example.todonotioncompose.network.TodoApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*
import android.content.Context
import com.example.todonotioncompose.data.Token.OfflineTokensRepository
import com.example.todonotioncompose.data.Token.TokensRepository
import com.example.todonotioncompose.network.UserApiService

/**
 * Dependency Injection container at the application level.
 */
interface AppContainer {
    val todosRepository: TodosRepository
    val keywordsRepository: KeywordsRepository
    val tokensRepository: TokensRepository

    val usersRepository: UsersRepository
}



/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 * https://developer.android.com/training/dependency-injection/manual
 */
class DefaultAppContainer(private val context: Context) : AppContainer {
    private val baseUrl = "https://pixabay.com/api/"

    private val localBaseUrl = "http://10.0.2.2:1717/api/"


    /**
     * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
     */
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     * https://github.com/JakeWharton/retrofit2-kotlinx-serialization-converter/blob/trunk/README.md
     * https://dev.to/vtsen/simple-rest-api-android-app-in-kotlin-various-http-client-library-implementations-11i2?comments_sort=latest
     */
    private val retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(baseUrl)
        .build()

    private val localRetrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(localBaseUrl)
        .build()
    /*
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()
    */
    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: TodoApiService by lazy {
        retrofit.create(TodoApiService::class.java)
    }

    private val localRetrofitService: UserApiService by lazy {
        localRetrofit.create(UserApiService::class.java)
    }

    /**
     * DI implementation for photos repository
     */
    override val todosRepository: TodosRepository by lazy {
        NetworkTodosRepository(retrofitService)
    }

    override val usersRepository: UsersRepository by lazy {
        NetworkUsersRepository(localRetrofitService)
    }

    override val keywordsRepository: KeywordsRepository by lazy {
        OfflineKeywordsRepository(TodoNotionDatabase.getDatabase(context).keyDao())
    }
    override val tokensRepository: TokensRepository by lazy {
        OfflineTokensRepository(TodoNotionDatabase.getDatabase(context).tokenDao())
    }
}

