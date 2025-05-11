package org.example.logic.useCase

import org.example.logic.models.ProjectState
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.BlankInputException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateProjectStateUseCase(
    private val projectStateRepository: ProjectStateRepository,
    private val projectRepository: ProjectRepository,
) {
    suspend operator fun invoke(
        projectId: Uuid,
        stateName: String,
    ): ProjectState{
        checkInputValidation(stateName)
        return projectStateRepository.createProjectState(
            ProjectState(
                title = stateName,
                projectId = projectId
            )
        )
    }

    private fun checkInputValidation(stateName: String) {
        when {
            stateName.isBlank() -> throw BlankInputException()
        }
    }
}
