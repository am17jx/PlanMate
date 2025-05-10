package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.BlankInputException

class LoginUserUseCase(private val authenticationRepository: AuthenticationRepository) {

    suspend operator fun invoke(username: String, password: String): User {
        when {
            username.isBlank() -> throw BlankInputException("Username is blank")
            password.isBlank() -> throw BlankInputException("Password is blank")
        }

        return authenticationRepository.login(username, password)

    }

}