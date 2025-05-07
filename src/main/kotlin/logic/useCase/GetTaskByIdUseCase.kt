package org.example.logic.useCase

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidInputException
import org.example.logic.utils.TaskNotFoundException

class GetTaskByIdUseCase(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String): Task {
        validateTaskId(taskId)
        return taskRepository.getTaskById(taskId).takeIf { it?.id == taskId }
            ?: throw TaskNotFoundException("No task found with id: $taskId")
    }

    private fun validateTaskId(taskId: String) {
        if (taskId.isBlank()) throw BlankInputException("Task ID cannot be blank")
        if (taskId.any { !(it.isLetterOrDigit() || it == '-') })
            throw InvalidInputException("Task ID should be alphanumeric")
    }

}