package org.example.data.repository.sources.remote

import org.example.logic.models.User

interface RemoteAuthenticationDataSource {

    suspend fun saveUser(user: User)
    suspend fun getAllUsers(): List<User>
    suspend fun login(username: String, hashedPassword: String): User
    suspend fun getCurrentUser(): User?
}