package org.example.presentation.screens

import kotlinx.coroutines.runBlocking
import org.example.logic.models.AuditLogEntityType
import org.example.logic.models.Project
import org.example.logic.useCase.*
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.*
import org.example.presentation.role.ProjectScreensOptions
import org.koin.java.KoinJavaComponent.getKoin
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ProjectsOverviewUI(
    private val onNavigateToShowProjectTasksUI: (id: String) -> Unit,
    private val onNavigateToProjectStatusUI: (id: String) -> Unit,
    private val onLogout: () -> Unit,
    private val onExit: () -> Unit,
    private val projectScreensOptions: ProjectScreensOptions,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getEntityAuditLogsUseCase: GetEntityAuditLogsUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
    private val tablePrinter: TablePrinter,
) {
    private val options: Map<String, String> = projectScreensOptions.showAllProjectsOptions()

    init {
        showMainMenu()
    }

    private fun showAllProjects() =
        runBlocking {
            try {
                val projects = getAllProjectsUseCase()

                if (projects.isEmpty()) {
                    displayNoProjectsMessage()
                    return@runBlocking
                }

                showProjectsInTable(projects)
            } catch (e: NoProjectsFoundException) {
                displayLoadingError(e)
            } catch (e: Exception) {
                displayLoadingError(e)
            }
        }

    private fun displayNoProjectsMessage() {
        viewer.display("No projects found.")
    }

    private fun showProjectsInTable(projects: List<Project>) {
        val projectIds = projects.map { it.id }
        val projectNames = projects.map { it.name }
        tablePrinter.printTable(
            headers = listOf("Project ID", "Project Name"),
            columnValues = listOf(projectIds, projectNames),
        )
    }

    private fun displayLoadingError(e: Exception) {
        viewer.display("Failed to load projects: ${e.message}")
    }

    private fun showMainMenu() {
        while (true) {
            showAllProjects()
            viewer.display("\n=== Project Menu ===")
            val sortedOptions = options.toSortedMap()
            sortedOptions.forEach { option ->
                option.value.let {
                    viewer.display(it)
                }
            }

            viewer.display("Select an option:")
            val input = reader.readString()
            val selectedOption = MainMenuOption.fromKey(input)

            when (selectedOption) {
                MainMenuOption.SHOW_DETAILS -> showProjectDetails()
                MainMenuOption.UPDATE_PROJECT -> updateProject()
                MainMenuOption.DELETE_PROJECT -> deleteProject()
                MainMenuOption.SHOW_PROJECT_LOGS -> showProjectLogsInTable()
                MainMenuOption.LOGOUT -> {
                    logout()
                    return
                }

                MainMenuOption.EXIT -> onExit()

                null -> viewer.display("Invalid input. Please try again.")
            }
        }
    }

    private fun showProjectLogsInTable() =
        runBlocking {
            try {
                viewer.display("Please enter the project ID:")
                val projectId = reader.readString()
                val projectLogs = getEntityAuditLogsUseCase(projectId, AuditLogEntityType.PROJECT)
                val actions = projectLogs.map { it.action }
                tablePrinter.printTable(
                    headers = listOf("Actions"),
                    columnValues = listOf(actions),
                )
            } catch (e: TaskNotFoundException) {
                viewer.display("Failed to load project logs: $TASK_NOT_FOUND_ERROR_MESSAGE")
            } catch (e: ProjectNotFoundException) {
                viewer.display("Failed to load project logs: $PROJECT_NOT_FOUND_ERROR_MESSAGE")
            } catch (e: BlankInputException) {
                viewer.display("Failed to load project logs: $BLANK_ENTITY_ID_ERROR_MESSAGE")
            } catch (e: Exception) {
                viewer.display("Failed to load project logs: ${e.message}")
            }
        }

    private fun logout() =
        runBlocking {
            logoutUseCase()
            onLogout()
        }

    private fun deleteProject() =
        runBlocking {
            try {
                viewer.display("Please enter the project ID:")
                val projectId = reader.readString()
                deleteProjectUseCase(projectId)
                viewer.display("Project deleted successfully.")
            } catch (e: Exception) {
                viewer.display("Failed to delete project: ${e.message}")
            }
        }

    private fun updateProject() {
        try {
            viewer.display("1 - Update project name")
            viewer.display("2 - Manage project status")
            val selected = UpdateProjectOption.fromKey(reader.readString())

            when (selected) {
                UpdateProjectOption.UPDATE_NAME -> updateProjectName()
                UpdateProjectOption.MANAGE_STATUS -> {
                    viewer.display("Please enter the project ID:")
                    val projectId = reader.readString()
                    onNavigateToProjectStatusUI(projectId)
                }

                null -> viewer.display("Invalid input.")
            }
        } catch (e: Exception) {
            viewer.display("Failed to update project: ${e.message}")
        }
    }

    private fun updateProjectName() =
        runBlocking {
            try {
                viewer.display("Please enter the project ID:")
                val projectId = reader.readString()

                viewer.display("Please enter new project name:")
                val newName = reader.readString()

                val project = getProjectByIdUseCase(projectId)
                val updatedProject = project.copy(name = newName)

                updateProjectUseCase(updatedProject)
                viewer.display("Project name updated successfully.")
            } catch (e: BlankInputException) {
                viewer.display("Failed to update project name: ${e.message}")
            } catch (e: ProjectNotChangedException) {
                viewer.display("Failed to update project name: $NO_CHANGES_DETECTED_EXCEPTION_MESSAGE")
            } catch (e: ProjectNotFoundException) {
                viewer.display("Failed to update project name: $PROJECT_NOT_FOUND_EXCEPTION_MESSAGE")
            } catch (e: Exception) {
                viewer.display("Failed to update project name: ${e.message}")
            }
        }

    private fun showProjectDetails() {
        viewer.display("Please enter the project ID:")
        val projectId = reader.readString()
        onNavigateToShowProjectTasksUI(projectId)
    }

    enum class MainMenuOption(
        val key: String,
    ) {
        SHOW_DETAILS("1"),
        UPDATE_PROJECT("2"),
        DELETE_PROJECT("3"),
        SHOW_PROJECT_LOGS("4"),
        LOGOUT("5"),
        EXIT("0"),
        ;

        companion object {
            fun fromKey(key: String): MainMenuOption? = entries.find { it.key == key }
        }
    }

    enum class UpdateProjectOption(
        val key: String,
    ) {
        UPDATE_NAME("1"),
        MANAGE_STATUS("2"),
        ;

        companion object {
            fun fromKey(key: String): UpdateProjectOption? = entries.find { it.key == key }
        }
    }

    companion object {
        fun create(
            onNavigateToShowProjectTasksUI: (id: String) -> Unit,
            onNavigateToProjectStatusUI: (id: String) -> Unit,
            onLogout: () -> Unit,
            onExit: () -> Unit,
            projectScreensOptions: ProjectScreensOptions,
        ): ProjectsOverviewUI =
            ProjectsOverviewUI(
                onNavigateToShowProjectTasksUI = onNavigateToShowProjectTasksUI,
                onNavigateToProjectStatusUI = onNavigateToProjectStatusUI,
                onLogout = onLogout,
                onExit = onExit,
                projectScreensOptions = projectScreensOptions,
                getAllProjectsUseCase = getKoin().get(),
                updateProjectUseCase = getKoin().get(),
                getProjectByIdUseCase = getKoin().get(),
                deleteProjectUseCase = getKoin().get(),
                getEntityAuditLogsUseCase = getKoin().get(),
                logoutUseCase = getKoin().get(),
                viewer = getKoin().get(),
                reader = getKoin().get(),
                tablePrinter = getKoin().get(),
            )

        const val PROJECT_NOT_FOUND_EXCEPTION_MESSAGE = "Project not found"
        const val NO_CHANGES_DETECTED_EXCEPTION_MESSAGE = "No changes detected ^_^"
        const val BLANK_ENTITY_ID_ERROR_MESSAGE = "Entity id cannot be blank"
        const val TASK_NOT_FOUND_ERROR_MESSAGE = "No task found with this id"
        const val PROJECT_NOT_FOUND_ERROR_MESSAGE = "No project found with this id"
    }
}
