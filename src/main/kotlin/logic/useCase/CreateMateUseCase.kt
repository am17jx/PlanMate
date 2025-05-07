package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidUserNameInputException
import org.example.logic.utils.UserAlreadyExistsException
import org.example.logic.utils.hashWithMD5

class CreateMateUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    operator suspend fun invoke(
        username: String,
        password: String,
    ): User {
        when {
            username.isBlank() -> throw BlankInputException("Username is blank")
            password.isBlank() -> throw BlankInputException("Password is blank")
            hasSpace(username) -> throw InvalidUserNameInputException("Username cannot contain spaces")
            isUserExists(username) -> throw UserAlreadyExistsException("User already exists")
            else -> {
                val hashedPassword = hashWithMD5(password)
                return authenticationRepository.createMate(username, hashedPassword)
            }
        }
    }

    private fun hasSpace(username: String) = username.any { it.isWhitespace() }

    private suspend fun isUserExists(username: String) = authenticationRepository.getAllUsers().any { it.username == username }
}
