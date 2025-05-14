package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository

class LoginUserUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val validation: Validation,
) {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): User {
        validation.validateLoginUsernameAndPasswordOrThrow(username, password)
        return authenticationRepository.loginWithPassword(username, password)
    }
}
