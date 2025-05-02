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
import org.example.logic.utils.UnauthorizedException
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class DeleteStateUseCase(
    private val projectRepository: ProjectRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val updateProjectUseCase: UpdateProjectUseCase,
) {
    operator fun invoke(
        stateId: String,
        projectId: String,
    ): Project {
        checkInputValidation(stateId, projectId)
        checkUserRole()
        val project = getProject(projectId)
        val updatedStates = removeState(project, stateId)
        val updatedProject =
            updateProjectUseCase(
                project.copy(
                    states = updatedStates,
                ),
            )
        return updatedProject
    }

    private fun removeState(
        project: Project,
        stateId: String,
    ): List<State> = project.states.filter { it.id != stateId }

    private fun getProject(projectId: String): Project =
        projectRepository.getProjectById(projectId)
            ?: throw ProjectNotFoundException("Project not found")

    private fun checkUserRole() {
        if (getCurrentUser().role != UserRole.ADMIN) throw UnauthorizedException("User is not an admin")
    }

    private fun checkInputValidation(
        stateId: String,
        projectId: String,
    ) {
        when {
            stateId.isBlank() -> throw BlankInputException("State id cannot be blank")
            projectId.isBlank() -> throw BlankInputException("Project id cannot be blank")
        }
    }

    private fun getCurrentUser() =
        authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException("No logged in user")
}
