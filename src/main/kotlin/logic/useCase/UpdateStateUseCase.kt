package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import org.example.logic.utils.StateNotFoundException
import kotlin.uuid.ExperimentalUuidApi

class UpdateStateUseCase(
    private val projectRepository: ProjectRepository,
    private val updateProjectUseCase: UpdateProjectUseCase,
) {
    @OptIn(ExperimentalUuidApi::class)
    suspend operator fun invoke(
        newStateName: String,
        stateId: String,
        projectId: String,
    ): Project {
        checkInputValidation(newStateName, stateId, projectId)
        val project = getProject(projectId)
        val updatedStates = updateState(project, stateId, newStateName)
        val updatedProject =
            updateProjectUseCase(
                project.copy(
                    states = updatedStates,
                ),
            )
        return updatedProject
    }

    private fun updateState(
        project: Project,
        stateId: String,
        newStateName: String,
    ): List<State> =
        project.states
            .map { state ->
                if (state.id == stateId) {
                    state.copy(title = newStateName)
                } else {
                    state
                }
            }.also { checkStateExists(it, stateId) }

    private fun checkStateExists(
        states: List<State>,
        stateId: String,
    ) {
        if (states.none { state -> state.id == stateId }) throw TaskStateNotFoundException()
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
