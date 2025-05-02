package org.example.presentation.screens

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType
import org.example.logic.models.Project
import org.example.logic.useCase.*
import org.example.logic.useCase.deleteProject.DeleteProjectUseCase
import org.example.presentation.role.ProjectScreensOptions
import org.koin.java.KoinJavaComponent.getKoin
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ProjectsOverviewUI(
    private val onNavigateToShowProjectTasksUI: (id: String) -> Unit,
    private val onNavigateToProjectStatusUI: (id: String) -> Unit,
    private val projectScreensOptions: ProjectScreensOptions,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
    private val deleteProjectUseCase: DeleteProjectUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val getEntityAuditLogsUseCase: GetEntityAuditLogsUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
    private val tablePrinter: TablePrinter,
    private val onNavigateBack: () -> Unit
) {
    private val options: Map<String, String> = projectScreensOptions.showAllProjectsOptions()

    init {
        showAllProjects()
        showMainMenu()
    }

    private fun showAllProjects() {
        try {
            val projects = getAllProjectsUseCase()

            if (projects.isEmpty()) {
                displayNoProjectsMessage()
                return
            }

            showProjectsInTable(projects)

        } catch (e: Exception) {
            displayLoadingError(e)
        }
    }

    private fun displayNoProjectsMessage() {
        viewer.display("No projects found.")
    }

    private fun showProjectsInTable(projects:List<Project>){
        val projectIds= projects.map { it.id }
        val projectNames = projects.map { it.name }
        tablePrinter.printTable(
            headers = listOf("Project ID", "Project Name"),
            columnValues = listOf(projectIds,projectNames),
        )
    }


    private fun displayLoadingError(e: Exception) {
        viewer.display("Failed to load projects: ${e.message}")
    }


    private fun showMainMenu() {
        while (true) {
            viewer.display("\n=== Project Menu ===")
            val sortedOptions=options.toSortedMap()
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
                MainMenuOption.BACK -> {
                    backToLogin()
                    return
                }

                null -> viewer.display("Invalid input. Please try again.")
            }
        }
    }

    private fun showProjectLogsInTable() {
        try {
            val projectId = reader.readString()
            val projectLogs = getEntityAuditLogsUseCase(projectId, AuditLogEntityType.PROJECT)
            val actions = projectLogs.map { it.action }
            tablePrinter.printTable(
                headers = listOf("Actions"),
                columnValues = listOf( actions)
            )
        }catch (e: Exception){
            viewer.display("Failed to load project logs: ${e.message}")
        }
    }

    private fun backToLogin() {
            onNavigateBack()
    }

    private fun deleteProject() {
        try {
            viewer.display("Please enter the project ID:")
            val projectId = reader.readString()
            deleteProjectUseCase(projectId)
            viewer.display("Project deleted successfully. [Stub implementation]")
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

    private fun updateProjectName() {
        try {
            viewer.display("Please enter the project ID:")
            val projectId = reader.readString()

            viewer.display("Please enter new project name:")
            val newName = reader.readString()

            val project = getProjectByIdUseCase(projectId)
            val updatedProject = project.copy(name = newName)

            updateProjectUseCase(updatedProject)
            viewer.display("Project name updated successfully.")
        } catch (e: Exception) {
            viewer.display("Failed to update project name: ${e.message}")
        }
    }

    private fun showProjectDetails() {
            viewer.display("Please enter the project ID:")
            val projectId = reader.readString()
            onNavigateToShowProjectTasksUI(projectId)
    }

    enum class MainMenuOption(val key: String) {
        SHOW_DETAILS("1"),
        UPDATE_PROJECT("2"),
        DELETE_PROJECT("3"),
        SHOW_PROJECT_LOGS("4"),
        BACK("5");

        companion object {
            fun fromKey(key: String): MainMenuOption? = entries.find { it.key == key }
        }
    }

    enum class UpdateProjectOption(val key: String) {
        UPDATE_NAME("1"),
        MANAGE_STATUS("2");

        companion object {
            fun fromKey(key: String): UpdateProjectOption? = entries.find { it.key == key }
        }
    }

    companion object {
        fun create(
            onNavigateToShowProjectTasksUI: (id: String) -> Unit,
            onNavigateToProjectStatusUI: (id: String) -> Unit,
            onNavigateBack: () -> Unit,
            projectScreensOptions: ProjectScreensOptions
        ): ProjectsOverviewUI {
            return ProjectsOverviewUI(
                onNavigateToShowProjectTasksUI = onNavigateToShowProjectTasksUI,
                onNavigateToProjectStatusUI = onNavigateToProjectStatusUI,
                projectScreensOptions = projectScreensOptions,
                getAllProjectsUseCase = getKoin().get(),
                updateProjectUseCase = getKoin().get(),
                getProjectByIdUseCase = getKoin().get(),
                deleteProjectUseCase = getKoin().get(),
                getEntityAuditLogsUseCase = getKoin().get(),
                viewer = getKoin().get(),
                reader = getKoin().get(),
                tablePrinter = getKoin().get(),
                onNavigateBack = onNavigateBack
            )
        }
    }
}
