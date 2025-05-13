package org.example.logic.useCase

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetTasksByProjectStateUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(stateId: Uuid): List<Task>{
        return taskRepository.getTasksByProjectState(stateId)
    }
}
