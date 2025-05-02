package org.example.logic.useCase.createProject

import kotlinx.datetime.Clock
import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionalCommand
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

        return createAndLogProject(projectName)
    }

    private fun createAndLogProject(projectName: String): Project {
        val projectId = Uuid.random().getCroppedId()
        val audit = createLog(projectId, projectName)
        val newProject =
            Project(
                id = projectId,
                name = projectName,
                states = emptyList(),
                auditLogsIds = listOf(audit.id),
            )
        val auditCommand = CreateAuditLogCommand(auditLogRepository, audit)
        val projectCreateCommand = ProjectCreateCommand(projectRepository, newProject)
        val createProjectCommandTransaction = TransactionalCommand(
            listOf(projectCreateCommand, auditCommand),
            ProjectCreationFailedException(PROJECT_CREATION_FAILED_EXCEPTION_MESSAGE)
        )
        try {
            createProjectCommandTransaction.execute()
        } catch (e: ProjectCreationFailedException) {
            throw e
        }

        return newProject

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

    private fun createLog(projectId: String, projectName: String): AuditLog {
        val currentTime = Clock.System.now()
        return AuditLog(
            id = Uuid.random().getCroppedId(),
            userId = getCurrentUser().id,
            action = "User ${getCurrentUser().username} created project $projectName at $currentTime",
            timestamp = currentTime.epochSeconds,
            entityType = AuditLogEntityType.PROJECT,
            entityId = projectId,
            actionType = AuditLogActionType.CREATE,
        )
    }

    companion object {
        const val BLANK_INPUT_EXCEPTION_MESSAGE = "Project name cannot be blank"
        const val PROJECT_CREATION_FAILED_EXCEPTION_MESSAGE = "Failed to create project"
        const val NO_LOGGED_IN_USER_EXCEPTION_MESSAGE = "No logged-in user found"
        const val UNAUTHORIZED_EXCEPTION_MESSAGE = "Only admins can create projects"
        const val AUDIT_INPUT_EXCEPTION_MESSAGE = "Failed to create audit log"
        const val PROJECT_NAME_LENGTH_EXCEPTION_MESSAGE = "Project name should not exceed 16 characters"
    }
}
