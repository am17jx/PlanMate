package org.example.data.repository.sources.local

import org.example.logic.models.User

interface LocalAuthenticationDataSource {
    fun saveUser(user: User)

    fun getAllUsers(): List<User>

    fun login(
        username: String,
        hashedPassword: String,
    ): User

    fun logout()

    fun getCurrentUser(): User?
}
