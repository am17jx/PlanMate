package org.example.presentation.screens

import kotlinx.coroutines.runBlocking
import org.example.logic.useCase.CreateMateUseCase
import org.example.logic.utils.*
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class CreateUserUi(
    private val createMateUseCase: CreateMateUseCase,
    private val reader: Reader,
    private val viewer: Viewer,
    private val onBack: () -> Unit
) {

    init {
        run()
    }

    private fun run() = runBlocking{
        viewer.display("\n===== Create New Mate User =====")
        viewer.display("Enter username: ")
        val username = reader.readString()

        viewer.display("Enter password: ")
        val password = reader.readString()

        try {
            val user = createMateUseCase(username, password)
            viewer.display("✅ User '${user.username}' created successfully with role: ${user.role}")
        } catch (e: BlankInputException) {
            viewer.display("❌ Error: Username or Password cannot be blank")
        } catch (e: InvalidUsernameException) {
            viewer.display("❌ Error: Username cannot contain spaces")
        } catch (e: UserAlreadyExistsException) {
            viewer.display("❌ Error: User already exists")
        } catch (e: Exception) {
            viewer.display("❌ Unexpected error: ${e.message}")
        }

        viewer.display("\nReturning...")
        onBack()
    }
}
