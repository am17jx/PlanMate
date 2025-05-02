package org.example.presentation.screens

import org.example.logic.useCase.*
import org.example.presentation.role.ProjectScreensOptions
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ShowAllProjectsUI(
    private val projectScreensOptions: ProjectScreensOptions,
    private val getAllProjectsUseCase: GetAllProjectsUseCase,
    private val updateProjectUseCase: UpdateProjectUseCase,
//    private val deleteProject:DeleteProjectUseCase,
    private val createStateUseCase: CreateStateUseCase,
    private val updateStateUseCase: UpdateStateUseCase,
    private val deleteStateUseCase: DeleteStateUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val navigateToShowProjectTasks: (id: String) -> Unit,
    private val viewer: Viewer,
    private val reader: Reader,
    private val onBack: () -> Unit
) {
    private var options: List<Map<String, String>> = projectScreensOptions.showAllProjectsOptions()

    init {
        showAllProjects()
        showMenu()
    }

    fun showAllProjects() {
        getAllProjectsUseCase().forEach { project ->
            println("-${project.id} : ${project.name}")
        }
        if (getAllProjectsUseCase().isEmpty()) {
            println("No projects found.")
        }
    }

    fun showMenu() {
        options.forEach { optionMap ->
            optionMap.forEach { (_, value) ->
                println(value)
            }
        }
        val input = readln()
        val isValid = options.any { it.containsKey(input) }
        if (!isValid) {
            println("Invalid input.")
        } else {
            when (input) {
                "1" -> showProjectDetails()
                "2" -> updateProject()
                "3" -> deleteProject()
                "4" -> backToLogin()
            }
        }
    }

    private fun backToLogin() {
        val userInput = reader.readString()
        if (userInput == "0") {
            onBack()
        }
    }

    private fun deleteProject() {
        viewer.display("Please enter the project ID:")
        val projectId = reader.readString()
        TODO("Call deleteProjectUseCase with $projectId")
    }

    private fun updateProject() {
        showUpdateProjectMenu()
        when (readln()) {
            "1" -> {
                updateProjectName()
            }

            "2" -> {
                manageProjectStatus()
            }

            else -> {
                viewer.display("Invalid input.")
            }
        }
    }

    private fun manageProjectStatus() {
        showProjectStates()
        viewer.display("1- Create project state")
        viewer.display("2- Update project state")
        viewer.display("3- Delete project state")
        when (readln()) {
            "1" -> {
                createProjectState()
            }

            "2" -> {
                updateProjectState()
            }

            "3" -> {
                deleteProjectState()
            }

            else -> {
                viewer.display("Invalid input.")
            }
        }
    }

    private fun showProjectStates() {
        val projectId = reader.readString()
        val project = getProjectByIdUseCase(projectId)
        if (project.states.isEmpty()) {
            viewer.display("No states found for this project.")
        }
        project.states.forEach {
            viewer.display("State ID: ${it.id}, State Name: ${it.title}")
        }

    }

    private fun deleteProjectState() {
        viewer.display("Please enter the project ID:")
        val projectId = reader.readString()
        viewer.display("Please enter the state ID:")
        val stateId = reader.readString()
        deleteStateUseCase(stateId, projectId)

    }

    //need to modify
    private fun updateProjectState() {
        viewer.display("Please enter the project ID:")
        val projectId = reader.readString()
        viewer.display("Please enter the state ID:")
        val stateId = reader.readString()
        viewer.display("Please enter the new state name:")
        val newStateName = reader.readString()
        updateStateUseCase(newStateName, stateId, projectId)
    }

    private fun createProjectState() {
        viewer.display("Please enter the project ID:")
        val projectId = reader.readString()
        viewer.display("Please enter the state name:")
        val stateName = reader.readString()
        createStateUseCase(projectId, stateName)
    }

    private fun updateProjectName() {
        viewer.display("Please enter the project ID:")
        val projectId = reader.readString()
        viewer.display("Please enter new project name:")
        val newProjectName = reader.readString()
        val oldProject = getProjectByIdUseCase(projectId)
        val updatedProject = oldProject.copy(name = newProjectName)
        updateProjectUseCase(updatedProject)
    }

    private fun showUpdateProjectMenu() {
        viewer.display("1 - Update project name")
        viewer.display("2 - Manage project status")
    }

    private fun showProjectDetails() {
        viewer.display("Please enter the project ID:")
        val projectId = reader.readString()
        navigateToShowProjectTasks(projectId)
    }
}
