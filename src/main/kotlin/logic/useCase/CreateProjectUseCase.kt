package org.example.logic.useCase

import kotlinx.datetime.Clock
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectCreationFailedException
import org.example.logic.utils.formattedString
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val currentUserUseCase: GetCurrentUserUseCase,
) {
    suspend operator fun invoke(projectName: String): Project {
        checkInputValidation(projectName)

        return createAndLogProject(projectName)
    }

    private suspend fun createAndLogProject(projectName: String): Project {
        val projectId = Uuid.random().getCroppedId()
        val audit = createLog(projectId, projectName, currentUserUseCase())
        val newProject =
            Project(
                id = projectId,
                name = projectName,
                states = getDefaultStates(),
                auditLogsIds = listOf(audit.id),
            )

        auditLogRepository.createAuditLog(audit)
        projectRepository.createProject(newProject)
        return newProject
    }

    private fun getDefaultStates() =
        listOf(
            State(Uuid.random().getCroppedId(), DEFAULT_TO_DO_STATE_NAME),
            State(Uuid.random().getCroppedId(), DEFAULT_IN_PROGRESS_STATE_NAME),
            State(Uuid.random().getCroppedId(), DEFAULT_DONE_STATE_NAME),
        )

    private fun checkInputValidation(projectName: String) {
        when {
            projectName.isBlank() -> throw BlankInputException(BLANK_INPUT_EXCEPTION_MESSAGE)
            projectName.length > 16 -> throw ProjectCreationFailedException(PROJECT_NAME_LENGTH_EXCEPTION_MESSAGE)
        }
    }

    private fun createLog(
        projectId: String,
        projectName: String,
        user: User,
    ): AuditLog {
        val currentTime = Clock.System.now()
        return AuditLog(
            id = Uuid.random().getCroppedId(),
            userId = user.id,
            action = "User ${user.username} created project $projectName at ${currentTime.formattedString()}",
            createdAt = currentTime,
            entityType = AuditLogEntityType.PROJECT,
            entityId = projectId,
            actionType = AuditLogActionType.CREATE,
        )
    }

    companion object {
        const val BLANK_INPUT_EXCEPTION_MESSAGE = "Project name cannot be blank"
        const val PROJECT_CREATION_FAILED_EXCEPTION_MESSAGE = "Failed to create project"
        const val PROJECT_NAME_LENGTH_EXCEPTION_MESSAGE = "Project name should not exceed 16 characters"
        const val DEFAULT_TO_DO_STATE_NAME = "To Do"
        const val DEFAULT_IN_PROGRESS_STATE_NAME = "In Progress"
        const val DEFAULT_DONE_STATE_NAME = "Done"
    }
}
