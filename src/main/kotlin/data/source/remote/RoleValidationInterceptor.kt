package org.example.data.source.remote

import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.NoLoggedInUserException
import org.example.logic.utils.UnauthorizedException


class RoleValidationInterceptor(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend fun <T> validateRole(requiredRoles: List<UserRole> = listOf(UserRole.ADMIN), operation: suspend () -> T): T {
        val currentUser = authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException("No logged-in user found")

        if (currentUser.role !in requiredRoles) {
            throw UnauthorizedException("User does not have the required role: $requiredRoles")
        }
        return operation()
    }

}