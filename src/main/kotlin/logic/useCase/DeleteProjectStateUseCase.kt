package org.example.logic.useCase

import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskDeletionFailedException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DeleteProjectStateUseCase(
    private val projectStateRepository: ProjectStateRepository,
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
                projectStateRepository.deleteProjectState(stateId)
            }
    }

}
