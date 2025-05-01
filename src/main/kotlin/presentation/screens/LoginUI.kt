package org.example.presentation.screens

import org.example.logic.models.UserRole
import org.example.logic.useCase.LoginUserUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.UserNotFoundException

class LoginUI(
    private val onLoginSuccess: (UserRole) -> Unit,
    private val loginUserUseCase: LoginUserUseCase
) {

    init {
        run()
    }

    private fun run() {
        println("====================================")
        println(" Welcome to the Task Management System ")
        println("====================================")

        print("Enter username: ")
        val username = readln()

        print("Enter password: ")
        val password = readln()

        try {
            val user = loginUserUseCase(username, password)
            onLoginSuccess(user.role)
        } catch (e: BlankInputException) {
            println("Error: ${e.message}")
        } catch (e: UserNotFoundException) {
            println("Error: ${e.message}")
        } catch (e: Exception) {
            println("Unexpected error: ${e.message}")
        }
    }
}
