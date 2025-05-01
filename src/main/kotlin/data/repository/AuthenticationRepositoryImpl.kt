package org.example.data.repository

import data.source.local.contract.LocalAuthenticationDataSource
import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val localAuthenticationDataSource: LocalAuthenticationDataSource
): AuthenticationRepository {
    override fun getCurrentUser(): User? {
        TODO("Not yet implemented")
    }

    override fun createMate(username: String, hashedPassword: String): User {
        TODO("Not yet implemented")
    }

    override fun login(username: String, hashedPassword: String): User {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): List<User> {
        TODO("Not yet implemented")
    }
}