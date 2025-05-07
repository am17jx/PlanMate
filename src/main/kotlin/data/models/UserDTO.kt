package org.example.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.example.logic.models.UserRole

@Serializable
data class UserDTO(
    val _id: String,
    val username: String,
    val password: String,
    val role: String
)
