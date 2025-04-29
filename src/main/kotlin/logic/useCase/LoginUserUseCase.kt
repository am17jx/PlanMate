package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository

class LoginUserUseCase(private val authenticationRepository: AuthenticationRepository) {

    operator fun invoke(username: String, password: String): User {

        return User("", username, password, UserRole.USER)
    }
}