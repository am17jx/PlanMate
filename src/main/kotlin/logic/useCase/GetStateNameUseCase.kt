package org.example.logic.useCase

import org.example.logic.models.Task
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.TaskStateNotFoundException

class GetStateNameUseCase(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val projectStateRepository: ProjectStateRepository
) {
    suspend operator fun invoke(taskId: String): String {
        val task: Task = getTaskByIdUseCase(taskId)
        projectStateRepository.getProjectStateById(task.stateId)
        return projectStateRepository.getProjectStateById(task.stateId)?.title
            ?: throw TaskStateNotFoundException()
    }
}