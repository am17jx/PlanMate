package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectStateRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectStatesUseCase(
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val projectStateRepository: ProjectStateRepository
) {
    suspend operator fun invoke(projectId: Uuid): List<State> {
        val project: Project = getProjectByIdUseCase(projectId)
        return projectStateRepository.getProjectTaskStates(project.projectStateIds)
    }
}
