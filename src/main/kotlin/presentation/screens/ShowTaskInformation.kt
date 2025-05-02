package org.example.presentation.screens

import org.example.logic.models.AuditLogEntityType
import org.example.logic.models.Task
import org.example.logic.useCase.GetEntityAuditLogsUseCase
import org.example.logic.useCase.GetStateNameUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.useCase.deleteTask.DeleteTaskUseCase
import org.example.logic.useCase.updateTask.UpdateTaskUseCase
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ShowTaskInformation(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getStateNameUseCase: GetStateNameUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val getEntityAuditLogsUseCase: GetEntityAuditLogsUseCase,
    private val viewer: Viewer,
    private val reader: Reader,
) {
    fun showTaskInformation(taskId: String){
        var isRunning = true
        while (isRunning){
            try {
                val task = getTaskByIdUseCase(taskId)
                val stateName = getStateNameUseCase(taskId)

                displayTaskDetails(task, stateName)
                displayMenu()

                val choice = reader.readString().trim()
                when(choice){
                    "1" -> updateTask(task)
                    "2" -> {
                        deleteTask(taskId)
                        isRunning = false
                    }
                    "3" -> showTaskLogs(taskId)
                    "4" -> {
                        viewer.display("Exiting...")
                        isRunning = false
                    }
                    else -> viewer.display("Invalid choice. Please try again.")
                }
            } catch (e: Exception) {
                viewer.display("Error: ${e.message}")
                isRunning=false
            }
        }
    }
    private fun displayTaskDetails(task: Task , stateName: String) {
        viewer.display("Task Information: ")
        viewer.display("stateId : ${task.stateId}")
        viewer.display("taskID: ${task.id}")
        viewer.display("Name: ${task.name}")
        viewer.display("Added By: ${task.addedBy}")
        viewer.display("State: $stateName")
        viewer.display("")
    }

    private fun displayMenu() {
        viewer.display("Choices:")
        viewer.display("1. Update Task")
        viewer.display("2. Delete Task")
        viewer.display("3. show task logs")
        viewer.display("4. Exit")
        viewer.display("Enter your choice: ")
    }

    private fun updateTask(task: Task){
        try {
            viewer.display("Enter new task name:")
            val newName = reader.readString().takeIf { it.isNotBlank() } ?: task.name
            viewer.display("Enter new state ID :")
            val newStateId = reader.readString().takeIf { it.isNotBlank() } ?: task.stateId
            val updatedTask = task.copy(name = newName, stateId = newStateId)
            updateTaskUseCase(task.id, updatedTask)
            viewer.display("Task updated successfully.")
        } catch (e: Exception) {
            viewer.display("Error updating task: ${e.message}")
        }
    }
    private fun deleteTask(taskId: String): Boolean {
        try {
            viewer.display("Do you want to delete this task? (y/n)")
            val confirmation = reader.readString().trim().lowercase()
            if (confirmation == "y") {
                deleteTaskUseCase(taskId)
                viewer.display("Task deleted successfully.")
                return true
            } else {
                viewer.display("Deletion cancelled.")
                return false
            }
        } catch (e: Exception) {
            viewer.display("Error deleting task: ${e.message}")
            return false
        }
    }

    private fun showTaskLogs(taskId: String) {
        try {
            val taskLogs = getEntityAuditLogsUseCase(taskId, AuditLogEntityType.TASK)
            if (taskLogs.isEmpty()) {
                viewer.display("No logs found for this task.")
                return
            }
            val actions = taskLogs.map { it.action }
            val tablePrinter = TablePrinter(viewer, reader)
            tablePrinter.printTable(
                headers = listOf("Actions"),
                columnValues = listOf(actions)
            )
        } catch (e: Exception) {
            viewer.display("Error fetching logs: ${e.message}")
        }
    }

}