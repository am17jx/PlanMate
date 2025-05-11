package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.ProjectNotFoundException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateProjectStateUseCase(
    private val projectStateRepository: ProjectStateRepository,
    private val projectRepository: ProjectRepository,
    private val validation: Validation,
) {
    suspend operator fun invoke(
        projectId: Uuid,
        stateName: String,
    ) {
        validation.validateInputNotBlankOrThrow(stateName)
        val project = getProject(projectId)
        val newState = State(title = stateName)

        projectStateRepository.createProjectState(newState)
        projectRepository.updateProject(project.copy(projectStateIds = project.projectStateIds + newState.id))

    }

    private suspend fun getProject(projectId: Uuid): Project =
        (
            projectRepository.getProjectById(projectId)
                ?: throw ProjectNotFoundException()
        )
}
