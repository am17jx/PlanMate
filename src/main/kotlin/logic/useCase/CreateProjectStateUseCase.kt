package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
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
    ): State{
        checkInputValidation(stateName)
        return projectStateRepository.createProjectState(
            State(
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
