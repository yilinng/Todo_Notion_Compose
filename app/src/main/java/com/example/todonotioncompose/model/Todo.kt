package com.example.todonotioncompose.model

//import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.squareup.moshi.Json
//@Serializable
data class Todo(
    val id: String = "",
    val pageURL: String = "",
    val type: String = "",
    val tags: String = "",
    val views: Int = 0,
    val downloads: Int = 0,
    val collections: Int = 0,
    val likes: Int = 0,
    val comments: Int = 0,
    val user_id: Int = 0,
    val user: String = "",
    val userImageURL: String = "",
    @Json(name = "webformatURL") val imgSrcUrl: String = ""

    )
