package org.example.logic.models

data class User(
    val id: String,
    val username: String,
    val password: String,
    val role: UserRole
)
