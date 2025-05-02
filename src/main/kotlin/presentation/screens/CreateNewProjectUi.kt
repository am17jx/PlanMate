package org.example.presentation.screens


import org.example.logic.useCase.CreateProjectUseCase
import org.example.logic.utils.*
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class CreateNewProjectUi(
    private val createProjectUseCase: CreateProjectUseCase,
    private val onBack: () -> Unit,
    private val reader: Reader,
    private val viewer: Viewer
) {
    init {
        run()
    }

    private fun run() {
        viewer.display("\n===== Create New Project =====")
        viewer.display("Enter project name: ")

        val projectName = reader.readString()

        try {
            val project = createProjectUseCase(projectName)
            viewer.display("✅ Project '${project.name}' created successfully with ID: ${project.id}")
        } catch (e: BlankInputException) {
            viewer.display("❌ Error: ${e.message}")
        } catch (e: ProjectCreationFailedException) {
            viewer.display("❌ Error: ${e.message}")
        } catch (e: NoLoggedInUserException) {
            viewer.display("❌ Error: ${e.message}")
        } catch (e: UnauthorizedException) {
            viewer.display("❌ Error: ${e.message}")
        } catch (e: AuditInputException) {
            viewer.display("❌ Error: ${e.message}")
        } catch (e: Exception) {
            viewer.display("❌ Unexpected error: ${e.message}")
        }

        viewer.display("\nReturning to Admin Home...")
        onBack()
    }
}
