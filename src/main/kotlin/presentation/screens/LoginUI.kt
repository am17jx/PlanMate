package org.example.presentation.screens

import org.example.logic.models.UserRole
import org.example.logic.useCase.LoginUserUseCase

class LoginUI(
    private val onNavigateToShowAllProjects: (userRole:UserRole) -> Unit,
    private val loginUserUseCase: LoginUserUseCase
) {
    init {
        printWelcomeMessage()
    }

    private fun printWelcomeMessage() {
        println("Welcome to the Task Management System!")
        println("Please log in to continue.")
        val type=UserRole.USER
        onNavigateToShowAllProjects(type)

    }

}