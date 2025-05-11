package org.example.logic.repositries

import org.example.logic.models.User

interface AuthenticationRepository {
    suspend fun getCurrentUser(): User?

    suspend fun createUser(
        username: String,
        password: String,
    ): User

    suspend fun login(
        username: String,
        password: String,
    ): User

    suspend fun logout()

    suspend fun getAllUsers(): List<User>
}
