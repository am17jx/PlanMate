package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidUserNameInputException

class CreateMateUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke(
        username: String,
        password: String,
    ): User {
        when {
            username.isBlank() -> throw BlankInputException("Username is blank")
            password.isBlank() -> throw BlankInputException("Password is blank")
            hasSpace(username) -> throw InvalidUserNameInputException("Username cannot contain spaces")
            else -> {
                return authenticationRepository.createMate(username, password)
            }
        }
    }

    private fun hasSpace(username: String) = username.any { it.isWhitespace() }

}
