package org.example.logic.useCase

import kotlinx.datetime.Clock
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.utils.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke(projectName: String): Project {
        checkUserRole()
        checkInputValidation(projectName)
        val newProject =
            Project(
                id = Uuid.random().getCroppedId(),
                name = projectName,
                states = emptyList(),
                auditLogsIds = emptyList(),
            )
        val audit = createLog(newProject)
        return projectRepository.createProject(newProject.copy(auditLogsIds = listOf(audit.id)))
    }

    private fun checkInputValidation(projectName: String) {
        when {
            projectName.isBlank() -> throw BlankInputException(BLANK_INPUT_EXCEPTION_MESSAGE)
            projectName.length > 16 -> throw ProjectCreationFailedException(PROJECT_NAME_LENGTH_EXCEPTION_MESSAGE)
        }
    }

    private fun checkUserRole() {
        if (getCurrentUser().role != UserRole.ADMIN) {
            throw UnauthorizedException(UNAUTHORIZED_EXCEPTION_MESSAGE)
        }
    }

    private fun getCurrentUser(): User =
        authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException(NO_LOGGED_IN_USER_EXCEPTION_MESSAGE)

    private fun createLog(project: Project): AuditLog {
        val currentTime = Clock.System.now()
        val auditLog =
            AuditLog(
                id = Uuid.random().getCroppedId(),
                userId = getCurrentUser().id,
                action = "User ${getCurrentUser().username} created project ${project.name} at $currentTime",
                timestamp = currentTime.epochSeconds,
                entityType = AuditLogEntityType.PROJECT,
                entityId = project.id,
                actionType = AuditLogActionType.CREATE,
            )

        return auditLogRepository.createAuditLog(auditLog)
    }

    companion object {
        const val BLANK_INPUT_EXCEPTION_MESSAGE = "Project name cannot be blank"
        const val NO_LOGGED_IN_USER_EXCEPTION_MESSAGE = "No logged-in user found"
        const val UNAUTHORIZED_EXCEPTION_MESSAGE = "Only admins can create projects"
        const val PROJECT_NAME_LENGTH_EXCEPTION_MESSAGE = "Project name should not exceed 16 characters"
    }
}
