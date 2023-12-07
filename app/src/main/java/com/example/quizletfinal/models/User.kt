package com.example.quizletfinal.models
data class User(
    val username: String = "",
    val email: String = "",
    val profileImage: String = ""
) {
    // Add a no-argument constructor
    constructor() : this("", "", "")
}
