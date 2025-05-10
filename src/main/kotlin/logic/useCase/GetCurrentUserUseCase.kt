package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.NoLoggedInUserException
import org.example.logic.utils.UserNotFoundException

class GetCurrentUserUseCase(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): User {
        return authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException()
    }
}