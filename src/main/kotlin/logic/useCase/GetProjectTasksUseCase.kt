package org.example.logic.useCase

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository

class GetProjectTasksUseCase(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(projectId: String): List<Task> {
        TODO("Not yet implemented")
    }
}
