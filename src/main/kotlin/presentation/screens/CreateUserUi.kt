package org.example.presentation.screens

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

    private fun run() {
        viewer.display("\n===== Create New Mate User =====")
        viewer.display("Enter username: ")
        val username = reader.readString()

        viewer.display("Enter password: ")
        val password = reader.readString()

        try {
            val user = createMateUseCase(username, password)
            viewer.display("✅ User '${user.username}' created successfully with role: ${user.role}")
        } catch (e: BlankInputException) {
            viewer.display("❌ Error: ${e.message}")
        } catch (e: InvalidUserNameInputException) {
            viewer.display("❌ Error: ${e.message}")
        } catch (e: UserAlreadyExistsException) {
            viewer.display("❌ Error: ${e.message}")
        } catch (e: Exception) {
            viewer.display("❌ Unexpected error: ${e.message}")
        }

        viewer.display("\nReturning...")
        onBack()
    }
}
