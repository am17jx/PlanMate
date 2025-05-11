package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.TaskStateRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectStatesUseCase(
    private val taskStateRepository: TaskStateRepository,
) {
    suspend operator fun invoke(projectId: Uuid): List<State> {
        return taskStateRepository.getProjectTaskStates(projectId)
    }
}
