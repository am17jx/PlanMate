package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.UserNotFoundException
import org.example.logic.utils.hashWithMD5

class LoginUserUseCase(private val authenticationRepository: AuthenticationRepository) {

    operator fun invoke(username: String, password: String): User {
        val hashedPassword = hashWithMD5(password)
        when {
            username.isBlank() -> throw BlankInputException("Username is blank")
            password.isBlank() -> throw BlankInputException("Password is blank")
            isUserNotFound(username, hashedPassword) -> throw UserNotFoundException("User not found")
            else -> return authenticationRepository.login(username, hashedPassword)
        }
    }

    private fun isUserNotFound(username: String, password: String) =
        authenticationRepository.getAllUsers().none { it.username == username && it.password == password }


}