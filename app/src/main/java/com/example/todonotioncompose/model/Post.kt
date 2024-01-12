package com.example.todonotioncompose.model

data class Post(
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val createDate: String = "",
    val updateDate: String? = null,
    val userId: String = ""
)
