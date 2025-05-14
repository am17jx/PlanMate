package org.example.data.repository.sources.remote

import org.example.logic.models.User

interface RemoteAuthenticationDataSource {
    suspend fun saveUser(user: User)

    suspend fun getAllUsers(): List<User>

    suspend fun loginWithPassword(
        username: String,
        hashedPassword: String,
    ): User

    suspend fun logout()

    suspend fun getCurrentUser(): User?
}
