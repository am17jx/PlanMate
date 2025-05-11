package org.example.logic.useCase

import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.TaskStateRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DeleteStateUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val taskRepository: TaskRepository,
    private val getProjectTasksUseCase: GetProjectTasksUseCase
) {
    suspend operator fun invoke(
        stateId: Uuid,
        projectId: Uuid,
    ) {
        getProjectTasksUseCase(projectId)
            .filter { it.stateId == stateId }
            .forEach { task ->
                taskRepository.deleteTask(task.id)
            }.also {
                taskStateRepository.deleteTaskState(stateId)
            }
    }

}
