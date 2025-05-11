package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository

class CreateMateUseCase(
    private val authenticationRepository: AuthenticationRepository,
    private val validation: Validation,
) {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): User {

        validation.validateCreateMateUsernameAndPasswordOrThrow(username, password)
        return authenticationRepository.createMate(username, password)
    }

}
