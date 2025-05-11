package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskStateRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateStateUseCase(
    private val taskStateRepository: TaskStateRepository,
) {
    suspend operator fun invoke(
        newStateName: String,
        stateId: Uuid,
        projectId: Uuid,
    ) {
        checkInputValidation(newStateName)
        taskStateRepository.updateTaskState(State(stateId, newStateName, projectId))
    }

    private fun checkInputValidation(newStateName: String) {
        when {
            newStateName.isBlank() -> throw BlankInputException()
        }
    }
}
