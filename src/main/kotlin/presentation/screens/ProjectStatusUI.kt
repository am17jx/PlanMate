package org.example.presentation.screens

import kotlinx.coroutines.runBlocking
import org.example.logic.useCase.CreateStateUseCase
import org.example.logic.useCase.DeleteStateUseCase
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.UpdateStateUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import org.koin.java.KoinJavaComponent.getKoin
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ProjectStatusUI(
    private val createStateUseCase: CreateStateUseCase,
    private val updateStateUseCase: UpdateStateUseCase,
    private val deleteStateUseCase: DeleteStateUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val projectId: String,
    private val viewer: Viewer,
    private val reader: Reader,
    private val onNavigateBack: () -> Unit
) {

    init {
        manageProjectStatus()
    }

    private fun manageProjectStatus() {
        while (true) {
            showProjectStates()
            displayMenu()

            val input = reader.readString()
            val selectedOption = StatusMenuOption.fromKey(input)

            when (selectedOption) {
                StatusMenuOption.CREATE -> createProjectState()
                StatusMenuOption.UPDATE -> updateProjectState()
                StatusMenuOption.DELETE -> deleteProjectState()
                StatusMenuOption.BACK -> {
                    onNavigateBack()
                    return
                }

                null -> viewer.display("Invalid input. Please try again.")
            }
        }
    }

    private fun displayMenu() {
        viewer.display("\n=== Manage Project States ===")
        StatusMenuOption.entries.forEach {
            viewer.display("${it.key}- ${it.label}")
        }
        viewer.display("Select an option:")
    }

    private fun showProjectStates() = runBlocking {
        try {
            val project = getProjectByIdUseCase(projectId)
            if (project.states.isEmpty()) {
                viewer.display("No states found for this project.")
            } else {
                project.states.forEach {
                    viewer.display("State ID: ${it.id}, State Name: ${it.title}")
                }
            }
        } catch (e: ProjectNotFoundException) {
            viewer.display("Failed to fetch project states: ${e.message}")
        }catch (e: InvalidInputException) {
            viewer.display("Failed to fetch project states: Project ID is invalid")
        }catch (e: Exception) {
            viewer.display("Failed to fetch project states: ${e.message}")
        }
    }

    private fun createProjectState() = runBlocking{
        try {
            viewer.display("Enter the new state name:")
            val stateName = reader.readString()
            createStateUseCase(projectId, stateName)
            viewer.display("State created successfully.")
        } catch (e: ProjectNotFoundException) {
            viewer.display("Failed to create state: Project not found")
        }catch (e: BlankInputException) {
            viewer.display("Input cannot be blank")
        }catch (e: Exception) {
            viewer.display("Failed to create state: ${e.message}")
        }
    }

    private fun updateProjectState() = runBlocking{
        try {
            viewer.display("Enter the state ID to update:")
            val stateId = reader.readString()
            viewer.display("Enter the new state name:")
            val newStateName = reader.readString()
            updateStateUseCase(newStateName, stateId, projectId)
            viewer.display("State updated successfully.")
        } catch (e: TaskStateNotFoundException) {
            viewer.display("Failed to update state: State not found")
        }catch (e: ProjectNotFoundException) {
            viewer.display("Failed to update state: Project not found")
        }catch (e: BlankInputException) {
            viewer.display("Input cannot be blank")
        }catch (e: Exception) {
            viewer.display("Failed to update state: ${e.message}")
        }
    }

    private fun deleteProjectState() = runBlocking{
        try {
            viewer.display("Enter the state ID to delete:")
            val stateId = reader.readString()
            deleteStateUseCase(stateId, projectId)
            viewer.display("State deleted successfully.")
        } catch (e: ProjectNotFoundException) {
            viewer.display("Failed to delete state: Project not found")
        } catch (e: BlankInputException) {
            viewer.display("Input cannot be blank")
        }catch (e: Exception) {
            viewer.display("Failed to delete state: ${e.message}")
        }
    }

    companion object {
        fun create(
            projectId: String,
            onNavigateBack: () -> Unit
        ): ProjectStatusUI {
            return ProjectStatusUI(
                projectId = projectId,
                onNavigateBack = onNavigateBack,
                createStateUseCase = getKoin().get(),
                updateStateUseCase = getKoin().get(),
                deleteStateUseCase = getKoin().get(),
                getProjectByIdUseCase = getKoin().get(),
                viewer = getKoin().get(),
                reader = getKoin().get()
            )
        }
    }

    enum class StatusMenuOption(val key: String, val label: String) {
        CREATE("1", "Create project state"),
        UPDATE("2", "Update project state"),
        DELETE("3", "Delete project state"),
        BACK("4", "Back to show all projects");

        companion object {
            fun fromKey(key: String): StatusMenuOption? = entries.find { it.key == key }
        }
    }
}
