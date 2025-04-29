package org.example.logic.useCase

import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository

class GetCurrentUserUseCase(
    private val authenticationRepository:AuthenticationRepository
) {
    operator fun invoke():User{
        TODO("Not yet implemented")
    }
}