package org.example.logic.useCase

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.TaskNotFoundException

class GetProjectTasksUseCase(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(projectId: String): List<Task> {
        checkInputValidation(projectId)
        return taskRepository
            .getAllTasks()
            .filter { isTaskForProject(it, projectId) }
            .takeIf { it.isNotEmpty() }
            ?: throw TaskNotFoundException(NO_TASKS_FOUND_ERROR_MESSAGE.plus(projectId))
    }

    private fun isTaskForProject(
        task: Task,
        projectId: String,
    ) = task.projectId == projectId

    private fun checkInputValidation(id: String) {
        when {
            id.isBlank() -> throw BlankInputException(PROJECT_ID_BLANK_ERROR_MESSAGE)
        }
    }

    companion object {
        const val PROJECT_ID_BLANK_ERROR_MESSAGE = "Project id cannot be blank"
        const val NO_TASKS_FOUND_ERROR_MESSAGE = "No tasks found for project with id"
    }
}
