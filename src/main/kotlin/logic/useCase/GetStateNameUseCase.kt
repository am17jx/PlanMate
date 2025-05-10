package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.models.Task
import org.example.logic.repositries.TaskStateRepository
import org.example.logic.utils.StateNotFoundException

class GetStateNameUseCase(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val taskStateRepository: TaskStateRepository
) {
    suspend operator fun invoke(taskId: String): String {
        val task: Task = getTaskByIdUseCase(taskId)
        taskStateRepository.getTaskStateById(task.stateId)
        return taskStateRepository.getTaskStateById(task.stateId)?.title
            ?: throw StateNotFoundException("State not found")
    }
}