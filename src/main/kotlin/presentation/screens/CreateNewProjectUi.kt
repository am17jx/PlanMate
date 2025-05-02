package org.example.presentation.screens

import org.example.logic.useCase.createProject.CreateProjectUseCase
import org.example.logic.utils.*

class CreateNewProjectUi(
    private val createProjectUseCase: CreateProjectUseCase,
    private val onBack: () -> Unit
) {
    init {
        run()
    }

    private fun run() {
        println("\n===== Create New Project =====")
        print("Enter project name: ")

        val projectName = readln()

        try {
            val project = createProjectUseCase(projectName)
            println("✅ Project '${project.name}' created successfully with ID: ${project.id}")
        } catch (e: BlankInputException) {
            println("❌ Error: ${e.message}")
        } catch (e: ProjectCreationFailedException) {
            println("❌ Error: ${e.message}")
        } catch (e: NoLoggedInUserException) {
            println("❌ Error: ${e.message}")
        } catch (e: UnauthorizedException) {
            println("❌ Error: ${e.message}")
        } catch (e: AuditInputException) {
            println("❌ Error: ${e.message}")
        } catch (e: Exception) {
            println("❌ Unexpected error: ${e.message}")
        }

        println("\nReturning to Admin Home...")
        onBack()
    }
}
