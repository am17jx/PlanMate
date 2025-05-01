package org.example.logic.useCase

import org.example.logic.models.*
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.utils.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateStateUseCase(
    private val projectRepository: ProjectRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val updateProjectUseCase: UpdateProjectUseCase,
) {
    operator fun invoke(
        projectId: String,
        stateName: String,
    ): Project {
        checkInputValidation(stateName, projectId)
        checkUserRole()
        val project = getProject(projectId)
        val newState =
            State(
                id = Uuid.random().toHexString(),
                title = stateName,
            )
        val updatedProject =
            updateProjectUseCase(
                project.copy(
                    states = project.states + newState,
                ),
            )
        return updatedProject
    }

    private fun getProject(projectId: String): Project =
        (
            projectRepository.getProjectById(projectId)
                ?: throw ProjectNotFoundException("Project not found")
        )

    private fun checkUserRole() {
        if (getCurrentUser().role != UserRole.ADMIN) throw UnauthorizedException("User is not an admin")
    }

    private fun checkInputValidation(
        stateName: String,
        projectId: String,
    ) {
        when {
            stateName.isBlank() -> throw BlankInputException("State name cannot be blank")
            projectId.isBlank() -> throw BlankInputException("Project id cannot be blank")
        }
    }

    private fun getCurrentUser() =
        authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException("No logged in user")
}
