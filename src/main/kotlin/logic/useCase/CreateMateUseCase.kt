package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidUsernameException

class CreateMateUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): User {
        when {
            username.isBlank() -> throw BlankInputException()
            password.isBlank() -> throw BlankInputException()
            hasSpace(username) -> throw InvalidUsernameException()
            else -> {
                return authenticationRepository.createMate(username, password)
            }
        }
    }

    private fun hasSpace(username: String) = username.any { it.isWhitespace() }

}
