package com.example.todonotioncompose.model

data class Flower (
    val total: Long,
    val totalHits: Long,
    val hits: List<Todo>
)