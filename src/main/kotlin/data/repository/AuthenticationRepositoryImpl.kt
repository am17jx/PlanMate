package org.example.data.repository

import org.example.data.repository.sources.remote.RemoteAuthenticationDataSource
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AuthenticationRepositoryImpl(
    private val remoteAuthenticationDataSource: RemoteAuthenticationDataSource
): AuthenticationRepository {

    override suspend fun getCurrentUser(): User? = remoteAuthenticationDataSource.getCurrentUser()

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun createMate(username: String, hashedPassword: String): User {
        val user = User(Uuid.random().getCroppedId(),username,hashedPassword, UserRole.USER)
        remoteAuthenticationDataSource.saveUser(user)
        return user
    }

    override suspend fun login(username: String, hashedPassword: String): User {
        return remoteAuthenticationDataSource.login(username, hashedPassword)
    }

    override suspend fun getAllUsers(): List<User> {
        return remoteAuthenticationDataSource.getAllUsers()
    }
}