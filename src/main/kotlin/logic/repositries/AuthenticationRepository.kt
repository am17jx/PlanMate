package org.example.logic.repositries

import org.example.logic.models.User

interface AuthenticationRepository {
    suspend fun getCurrentUser(): User?

    suspend fun createMate(
        username: String,
        hashedPassword: String,
    ): User

    suspend fun login(
        username: String,
        hashedPassword: String,
    ): User

    suspend fun getAllUsers(): List<User>
}
