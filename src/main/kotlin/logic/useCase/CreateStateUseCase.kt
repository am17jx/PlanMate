package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskStateRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateStateUseCase(
    private val taskStateRepository: TaskStateRepository,
) {
    suspend operator fun invoke(
        projectId: Uuid,
        stateName: String,
    ): State{
        checkInputValidation(stateName)
        return taskStateRepository.createTaskState(
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
