package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.hashWithMD5

class LoginUserUseCase(private val authenticationRepository: AuthenticationRepository) {

    suspend operator fun invoke(username: String, password: String): User {
        when {username.isBlank() || password.isBlank() -> throw BlankInputException() }

        val hashedPassword = hashWithMD5(password)
        return authenticationRepository.login(username, hashedPassword)

    }

}