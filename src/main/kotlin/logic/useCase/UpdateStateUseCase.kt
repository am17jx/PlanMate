package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskStateRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import kotlin.uuid.ExperimentalUuidApi

class UpdateStateUseCase(
    private val taskStateRepository: TaskStateRepository,
    private val projectRepository: ProjectRepository,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        newStateName: String,
        stateId: String,
        projectId: String,
    ) {
        checkInputValidation(newStateName, stateId, projectId)
        val project =getProject(projectId)
        checkStateExists(project.tasksStatesIds,stateId)
        getProject(projectId)

        taskStateRepository.updateTaskState(State(stateId, newStateName))
    }

    private fun checkStateExists(
        states: List<String>,
        stateId: String,
    ) {
        if (states.none { state -> state == stateId }) throw TaskStateNotFoundException()
    }

    private suspend fun getProject(projectId: String): Project =
        projectRepository.getProjectById(projectId)
            ?: throw ProjectNotFoundException()


    private fun checkInputValidation(
        newStateName: String,
        stateId: String,
        projectId: String,
    ) {
        when {
            newStateName.isBlank() -> throw BlankInputException()
            stateId.isBlank() -> throw BlankInputException()
            projectId.isBlank() -> throw BlankInputException()
        }
    }

}
