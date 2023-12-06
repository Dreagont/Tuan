package com.example.quizletfinal.models

data class Folder(
    val id: String,
    val title: String,
    val description: String,
    val topics: Map<String, Boolean> = emptyMap()
)