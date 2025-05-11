package org.example.logic.useCase

import org.example.logic.models.Task
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.TaskStateNotFoundException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetStateNameUseCase(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val projectStateRepository: ProjectStateRepository,
) {
    suspend operator fun invoke(taskId: Uuid): String {
        val task: Task = getTaskByIdUseCase(taskId)
        return projectStateRepository.getProjectStateById(task.stateId)?.title ?: throw TaskStateNotFoundException()
    }
}
