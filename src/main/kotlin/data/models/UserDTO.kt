package org.example.data.models

import org.example.logic.models.UserRole

data class UserDTO(
    val id: String,
    val username: String,
    val password: String,
    val role: UserRole
)
