package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectStateRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectStatesUseCase(
    private val projectStateRepository: ProjectStateRepository
) {
    suspend operator fun invoke(projectId: Uuid): List<State> {
        return projectStateRepository.getProjectStates(projectId)
    }
}
