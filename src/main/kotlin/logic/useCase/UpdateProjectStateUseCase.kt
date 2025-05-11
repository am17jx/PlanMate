package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateProjectStateUseCase(
    private val projectStateRepository: ProjectStateRepository,
) {
    suspend operator fun invoke(
        newStateName: String,
        stateId: Uuid,
        projectId: Uuid,
    ) {
        checkInputValidation(newStateName)
        projectStateRepository.updateProjectState(State(stateId, newStateName, projectId))
    }

    private fun checkInputValidation(newStateName: String) {
        when {
            newStateName.isBlank() -> throw BlankInputException()
        }
    }
}
