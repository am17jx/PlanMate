package org.example.logic.repositries

import org.example.logic.models.User

interface AuthenticationRepository {
    fun getCurrentUser(): User?
    fun createMate(username: String, hashedPassword: String): User
    fun login(username: String, hashedPassword: String): User
}