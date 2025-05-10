package org.example.presentation.screens

import kotlinx.coroutines.runBlocking
import org.example.logic.models.AuditLogEntityType
import org.example.logic.models.Project
import org.example.logic.models.Task
import org.example.logic.useCase.*
import org.koin.java.KoinJavaComponent.getKoin
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ShowTaskInformation(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getStateNameUseCase: GetStateNameUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getEntityAuditLogsUseCase: GetEntityAuditLogsUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
    private val tablePrinter: TablePrinter,
    private val onNavigateBack: () -> Unit,
) {
    fun showTaskInformation(taskId: String) = runBlocking {
        var isRunning = true
        while (isRunning) {
            try {
                val task = getTaskByIdUseCase(taskId)
                val stateName = getStateNameUseCase(taskId)
                val project = getProjectByIdUseCase(task.projectId)

                displayTaskDetails(task, stateName)
                displayMenu()

                when (reader.readString().trim()) {
                    "1" -> updateTask(task, project)
                    "2" -> {
                        deleteTask(taskId)
                        isRunning = false
                    }
                    "3" -> showTaskLogs(taskId)
                    "4" -> {
                        onNavigateBack
                    }
                    else -> viewer.display("Invalid choice. Please try again.")
                }
            } catch (e: Exception) {
                viewer.display("Error: ${e.message}")
                isRunning = false
            }
        }
    }
    private fun displayTaskDetails(task: Task, stateName: String) {
        val headers = listOf("Field", "Value")
        val rows = listOf(
            listOf("Name", task.name),
            listOf("Added By", task.addedBy),
            listOf("State", stateName),
        )

        val columnValues = List(headers.size) { colIndex -> rows.map { it[colIndex] } }

        viewer.display("Task Information:")
        tablePrinter.printTable(headers, columnValues)
        viewer.display("")
    }


    private fun displayMenu() {
        viewer.display("Select an option:")
        viewer.display("1. Update Task")
        viewer.display("2. Delete Task")
        viewer.display("3. Show Task Logs")
        viewer.display("4. Exit")
        viewer.display("Enter your choice:")
    }

    private fun updateTask(task: Task, project: Project) = runBlocking {
        try {
            viewer.display("Enter new task name:")
            val newName = reader.readString().takeIf { it.isNotBlank() } ?: task.name

            viewer.display("Select a new state from the following list:")

            val stateNames = project.states.map { it.title }
            val stateIds = project.states.map { it.id }
            val headers = listOf("Index", "State Name")
            val columnValues = listOf(
                stateNames.indices.map { (it + 1).toString() },
                stateNames
            )
            tablePrinter.printTable(headers, columnValues)

            viewer.display("Select a new state index:")
            val index = reader.readInt()
            val newStateId = if (index == null || index !in 1..stateIds.size) {
                viewer.display("Invalid index, keeping old state.")
                task.stateId
            } else {
                stateIds[index - 1]
            }

            val updatedTask = task.copy(name = newName, stateId = newStateId)
            updateTaskUseCase(task.id, updatedTask)
            viewer.display("Task updated successfully.")
        } catch (e: Exception) {
            viewer.display("Error updating task: ${e.message}")
        }
    }

    private fun deleteTask(taskId: String): Boolean = runBlocking {
        try {
            viewer.display("Do you want to delete this task? (y/n)")
            val confirmation = reader.readString().trim().lowercase()
            if (confirmation == "y") {
                deleteTaskUseCase(taskId)
                viewer.display("Task deleted successfully.")
                return@runBlocking true
            } else {
                viewer.display("Deletion cancelled.")
                return@runBlocking false
            }
        } catch (e: Exception) {
            viewer.display("Error deleting task: ${e.message}")
            return@runBlocking false
        }
    }

    private fun showTaskLogs(taskId: String) = runBlocking {
        try {
            val taskLogs = getEntityAuditLogsUseCase(taskId, AuditLogEntityType.TASK)
            if (taskLogs.isEmpty()) {
                viewer.display("No logs found for this task.")
                return@runBlocking
            }
            val actions = taskLogs.map { it.action }
            tablePrinter.printTable(
                headers = listOf("Actions"),
                columnValues = listOf(actions)
            )
        } catch (e: Exception) {
            viewer.display("Error fetching logs: ${e.message}")
        }
    }

    companion object {
        fun create(
            onNavigateBack: () -> Unit
        ): ShowTaskInformation {
            return ShowTaskInformation(
                getTaskByIdUseCase = getKoin().get(),
                onNavigateBack = onNavigateBack,
                getStateNameUseCase = getKoin().get(),
                updateTaskUseCase = getKoin().get(),
                deleteTaskUseCase = getKoin().get(),
                getEntityAuditLogsUseCase = getKoin().get(),
                getProjectByIdUseCase = getKoin().get(),
                viewer = getKoin().get(),
                reader = getKoin().get(),
                tablePrinter = getKoin().get()
            )
        }
    }
}
