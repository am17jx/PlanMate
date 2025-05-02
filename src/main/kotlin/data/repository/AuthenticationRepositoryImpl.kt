package org.example.data.repository

import data.source.local.contract.LocalAuthenticationDataSource
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthenticationRepositoryImpl(
    private val localAuthenticationDataSource: LocalAuthenticationDataSource
): AuthenticationRepository {

    private var currentUser: User? = null


    override fun getCurrentUser(): User? = currentUser

    @OptIn(ExperimentalUuidApi::class)
    override fun createMate(username: String, hashedPassword: String): User {
        localAuthenticationDataSource.saveUser(User(Uuid.random().getCroppedId(),username,hashedPassword, UserRole.USER))
        return User(Uuid.random().getCroppedId(),username,hashedPassword, UserRole.USER)
    }

    override fun login(username: String, hashedPassword: String): User {
        val userId = getUserId(username, hashedPassword)
        val userRole = getUserRole(username, hashedPassword)
        currentUser = User(userId, username, hashedPassword, userRole)
        return currentUser!!
    }

    override fun getAllUsers(): List<User> {
        return localAuthenticationDataSource.getAllUsers()
    }


    private fun getUserId(username: String, hashedPassword: String): String {
        return getAllUsers().first { it.username == username && it.password == hashedPassword }.id
    }

    private fun getUserRole(username: String, hashedPassword: String): UserRole {
        return getAllUsers().first { it.username == username && it.password == hashedPassword }.role
    }
}