package org.example.data.source.remote

import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.NoLoggedInUserException
import org.example.logic.utils.UnauthorizedAccessException


class RoleValidationInterceptor(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend fun <T> validateRole(requiredRoles: List<UserRole> = listOf(UserRole.ADMIN), operation: suspend () -> T): T {
        val currentUser = authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException()

        if (currentUser.role !in requiredRoles) {
            throw UnauthorizedAccessException()
        }
        return operation()
    }

}