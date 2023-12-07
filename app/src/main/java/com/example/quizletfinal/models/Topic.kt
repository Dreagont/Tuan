package com.example.quizletfinal.models

data class Topic(
    val id: String,
    val title: String,
    val description: String,
    val visibility: String,
    val folderId: String? = null,
    val cards: Map<String, Card> = mapOf()
)