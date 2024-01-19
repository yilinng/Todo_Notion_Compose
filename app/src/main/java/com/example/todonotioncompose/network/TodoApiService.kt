package com.example.todonotioncompose.network
import com.example.todonotioncompose.model.Todo

import com.example.todonotioncompose.model.Flower
import retrofit2.http.GET
import retrofit2.http.Query

interface TodoApiService {
    /**
     * Returns a [List] of [Todo] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("?key=40521554-653259fd6834861c55e904c4e")
    suspend fun getPhotos() : Flower

    //https://stackoverflow.com/questions/24100372/retrofit-and-get-using-parameters

    @GET("?key=40521554-653259fd6834861c55e904c4e")
    suspend fun getPhotosWithKey(@Query(value="q", encoded=true) q: String):Flower

}
