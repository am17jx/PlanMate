package org.example.logic.useCase.creatTask

import kotlinx.datetime.*
import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionalCommand
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.models.*
import org.example.logic.utils.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(
        name: String,
        projectId: String,
        stateId: String,
    ): Task {
        verifyNoBlankInputs(name, projectId, stateId)
        verifyProjectAndStateExist(projectId, stateId)
        val loggedInUser = getLoggedInUserOrThrow()
        return createAndLogTask(name, projectId, stateId, loggedInUser)
    }

    private fun createAndLogTask(taskName: String, projectId: String, stateId: String, loggedInUser: User): Task {
        val taskId = Uuid.random().toHexString()
        val taskAuditLog = createAuditLog(taskId, taskName, loggedInUser)
        val newTask = Task(
            id = taskId,
            name = taskName,
            stateId = stateId,
            projectId = projectId,
            addedBy = loggedInUser.id,
            auditLogsIds = listOf(taskAuditLog.id)
        )

        val auditCommand = CreateAuditLogCommand(auditLogRepository, taskAuditLog)
        val taskCreateCommand = TaskCreateCommand(taskRepository, newTask)
        val createTaskCommandTransaction = TransactionalCommand(
            listOf(taskCreateCommand, auditCommand),
            TaskNotCreatedException("Project Not changed")
        )
        try {
            createTaskCommandTransaction.execute()
        }catch (e :TaskNotCreatedException){
            throw e
        }

        return newTask

    }

    private fun createAuditLog(taskId: String, name: String, loggedInUser: User): AuditLog {
        val timestampNow = Clock.System.now()
        return AuditLog(
            id = Uuid.random().toHexString(),
            userId = loggedInUser.id,
            action = "user ${loggedInUser.username} created task $name at ${timestampNow.formattedString()}",
            timestamp = timestampNow.epochSeconds,
            entityType = AuditLogEntityType.TASK,
            entityId = taskId,
            actionType = AuditLogActionType.CREATE
        )
    }

    private fun getLoggedInUserOrThrow() = authenticationRepository.getCurrentUser() ?: throw UserNotFoundException(
        NO_LOGGED_IN_USER_ERROR_MESSAGE
    )

    private fun verifyProjectAndStateExist(projectId: String, stateId: String) {
        projectRepository.getProjectById(projectId)?.let { project ->
            if (project.states.none { it.id == stateId }) throw StateNotFoundException(NO_STATE_FOUND_ERROR_MESSAGE)
        } ?: throw ProjectNotFoundException(NO_PROJECT_FOUND_ERROR_MESSAGE)
    }

    private fun verifyNoBlankInputs(
        name: String,
        projectId: String,
        stateId: String,
    ) {
        when {
            name.isBlank() -> throw BlankInputException(BLANK_TASK_NAME_ERROR_MESSAGE)
            projectId.isBlank() -> throw BlankInputException(BLANK_PROJECT_ID_ERROR_MESSAGE)
            stateId.isBlank() -> throw BlankInputException(BLANK_STATE_ID_ERROR_MESSAGE)
        }
    }

    companion object {
        const val NO_STATE_FOUND_ERROR_MESSAGE = "No state found with this ID"
        const val NO_PROJECT_FOUND_ERROR_MESSAGE = "No project found with this ID"
        const val BLANK_TASK_NAME_ERROR_MESSAGE = "Task name cannot be blank"
        const val BLANK_PROJECT_ID_ERROR_MESSAGE = "Project id cannot be blank"
        const val BLANK_STATE_ID_ERROR_MESSAGE = "State id cannot be blank"
        const val NO_LOGGED_IN_USER_ERROR_MESSAGE = "User is not logged in"
        const val AUDIT_LOG_CREATION_FAILED_ERROR_MESSAGE = "Failed to create audit log"
    }
}