package org.example.logic.useCase

import org.example.logic.repositries.AuthenticationRepository

class LogoutUseCase(
    private val authenticationRepository: AuthenticationRepository,
) {
    suspend operator fun invoke() {
        authenticationRepository.logout()
    }
}
