package org.example.presentation.screens

import logic.useCase.CreateTaskUseCase
import org.example.logic.models.Project
import org.example.logic.models.Task
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.utils.ProjectNotFoundException
import org.koin.java.KoinJavaComponent.getKoin
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ShowProjectTasksUI(
    private val projectId: String,
    private val onNavigateToTaskDetails: (taskId: String) -> Unit,
    private val onNavigateBack: () -> Unit,
    private val getProjectTasksUseCase: GetProjectTasksUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val createTaskUseCase: CreateTaskUseCase,
    private val reader: Reader,
    private val viewer: Viewer,
    private val tablePrinter: TablePrinter
) {
    private lateinit var project: Project
    private lateinit var projectTasks: List<Task>

    init {
        loadProject()
    }

    private fun loadProject() {
        viewer.display("Loading...")
        try {
            project = getProjectByIdUseCase(projectId)
            loadTasks()
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun loadTasks() {
        try {
            projectTasks = getProjectTasksUseCase(projectId)
            displaySwimLanesTasksTable()
            getUserSelectedOption()
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun displaySwimLanesTasksTable() {
        if (project.states.isEmpty()) {
            viewer.display("<==========( No States Added yet )==========>")
        }
        val (statesHeaders, tasksColumns) = getTableHeadersAndColumns()
        tablePrinter.printTable(
            headers = statesHeaders,
            columnValues = tasksColumns,
        )
    }

    private fun getTableHeadersAndColumns(): Pair<List<String>, List<List<String>>> {
        val groupedTasksByState = project.states.map { state ->
            val tasksForState = projectTasks.filter { it.stateId == state.id }
            state to tasksForState
        }
        val statesHeaders = groupedTasksByState.map { it.first.title }
        val tasksColumns = groupedTasksByState.map { it.second.map { task -> "${task.name}(id: ${task.id})" } }
        return statesHeaders to tasksColumns
    }

    private fun getUserSelectedOption() {
        while (true) {
            displayOptions()
            val userInput = reader.readInt() ?: -1
            when (userInput) {
                0 -> {
                    return onNavigateBack()
                }

                1 -> {
                    return getSelectedTaskId()
                }

                2 -> {
                    if (project.states.isEmpty()) {
                        viewer.display("No project states added yet! Go back and update project with new states.")
                    } else {
                        startCreateTaskFlow()
                    }
                }

                else -> {
                    viewer.display("Invalid option. Please, try again!")
                }
            }
        }
    }

    private fun displayOptions() {
        viewer.display("=== Select Option to Continue ========")
        viewer.display("0. Go Back")
        viewer.display("1. View Task Details")
        viewer.display("2. Create New Task")
        viewer.display("======================================")
    }

    private fun getSelectedTaskId() {
        viewer.display("Enter Task Id: ")
        val userInput = reader.readString()
        if (projectTasks.any { it.id == userInput }) {
            onNavigateToTaskDetails(userInput)
        } else {
            viewer.display("Id is incorrect!")
            loadTasks()
        }
    }

    private fun startCreateTaskFlow() {
        val taskName = readTaskName()
        val stateId = readSelectedState()
        try {
            createTaskUseCase(taskName, projectId, stateId)
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun readTaskName(): String {
        while (true) {
            viewer.display("Enter Task Name: ")
            val name = reader.readString()
            if (name.isBlank()) {
                viewer.display("Name cannot be blank!")
            } else if (name.contains(",")) {
                viewer.display("Name cannot contain comma!")
            } else {
                return name
            }
        }
    }

    private fun readSelectedState(): String {
        viewer.display("Select a state from the following states:")
        project.states.forEachIndexed { index, state ->
            viewer.display("$index. ${state.title} (id = ${state.id})")
        }
        while (true) {
            viewer.display("Enter state ID: ")
            val userInput = reader.readString()
            if (project.states.any { it.id == userInput }) {
                return userInput
            } else {
                viewer.display("Invalid state ID! Please, try again")
            }
        }
    }

    private fun handleError(e: Exception) {
        when (e) {
            is ProjectNotFoundException -> {
                viewer.display(e.message)
                return onNavigateBack()
            }

            else -> {
                viewer.display(e.message)
            }
        }
    }

    companion object {
        fun create(
            projectId: String, onNavigateToTaskDetails: (taskId: String) -> Unit, onNavigateBack: () -> Unit
        ): ShowProjectTasksUI {
            return ShowProjectTasksUI(
                projectId = projectId,
                onNavigateToTaskDetails = onNavigateToTaskDetails,
                onNavigateBack = onNavigateBack,
                getProjectTasksUseCase = getKoin().get(),
                getProjectByIdUseCase = getKoin().get(),
                createTaskUseCase = getKoin().get(),
                reader = getKoin().get(),
                viewer = getKoin().get(),
                tablePrinter = getKoin().get()
            )
        }
    }
}