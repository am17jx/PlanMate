package org.example.logic.useCase

import org.example.logic.models.ProjectState
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateProjectStateUseCase(
    private val projectStateRepository: ProjectStateRepository,
    private val validation: Validation,
) {
    suspend operator fun invoke(
        newStateName: String,
        stateId: Uuid,
        projectId: Uuid,
    ) {
        validation.validateInputNotBlankOrThrow(newStateName)
        projectStateRepository.updateProjectState(ProjectState(stateId, newStateName, projectId))
    }
}
