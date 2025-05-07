package org.example.data.repository

import org.example.data.source.remote.contract.RemoteAuthenticationDataSource
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthenticationRepositoryImpl(
    private val remoteAuthenticationDataSource: RemoteAuthenticationDataSource
): AuthenticationRepository {
    private var currentUser: User? = null

    override suspend fun getCurrentUser(): User? = currentUser

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createMate(username: String, hashedPassword: String): User {
        val user = User(Uuid.random().getCroppedId(),username,hashedPassword, UserRole.USER)
        remoteAuthenticationDataSource.saveUser(user)
        return user
    }

    override suspend fun login(username: String, hashedPassword: String): User {
        val userId = getUserId(username, hashedPassword)
        val userRole = getUserRole(username, hashedPassword)
        currentUser = User(userId, username, hashedPassword, userRole)
        return currentUser!!
    }

    override suspend fun getAllUsers(): List<User> {
        return remoteAuthenticationDataSource.getAllUsers()
    }


    private suspend fun getUserId(username: String, hashedPassword: String): String {
        try {
            return getAllUsers().first { it.username == username && it.password == hashedPassword }.id
        } catch(e: NoSuchElementException) {
            throw NoSuchElementException("User not found")
        }

    }

    private suspend fun getUserRole(username: String, hashedPassword: String): UserRole {
        try {
            return getAllUsers().first { it.username == username && it.password == hashedPassword }.role
        } catch(e: NoSuchElementException) {
            throw NoSuchElementException("User not found")
        }
    }
}