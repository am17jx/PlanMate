package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.NoLoggedInUserException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.StateNotFoundException
import org.example.logic.utils.UnauthorizedException

class UpdateStateUseCase(
    private val projectRepository: ProjectRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val updateProjectUseCase: UpdateProjectUseCase,
) {
    operator fun invoke(
        newStateName: String,
        stateId: String,
        projectId: String,
    ): Project {
        checkInputValidation(newStateName, stateId, projectId)
        checkUserRole()
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
        if (states.none { state -> state.id == stateId }) throw StateNotFoundException("State not found")
    }

    private fun getProject(projectId: String): Project =
        projectRepository.getProjectById(projectId)
            ?: throw ProjectNotFoundException("Project not found")

    private fun checkUserRole() {
        if (getCurrentUser().role != UserRole.ADMIN) throw UnauthorizedException("User is not an admin")
    }

    private fun checkInputValidation(
        newStateName: String,
        stateId: String,
        projectId: String,
    ) {
        when {
            newStateName.isBlank() -> throw BlankInputException("State name cannot be blank")
            stateId.isBlank() -> throw BlankInputException("State id cannot be blank")
            projectId.isBlank() -> throw BlankInputException("Project id cannot be blank")
        }
    }

    private fun getCurrentUser() =
        authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException("No logged in user")
}
