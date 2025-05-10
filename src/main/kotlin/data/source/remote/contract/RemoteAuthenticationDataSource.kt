package org.example.data.source.remote.contract

import org.example.logic.models.User

interface RemoteAuthenticationDataSource {
    suspend fun saveUser(user: User)

    suspend fun getAllUsers(): List<User>

    suspend fun login(
        username: String,
        hashedPassword: String,
    ): User

    suspend fun logout()

    suspend fun getCurrentUser(): User?
}
