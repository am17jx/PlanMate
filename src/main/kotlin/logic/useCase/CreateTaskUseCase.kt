package logic.useCase

import kotlinx.datetime.*
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.utils.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) {
    suspend operator fun invoke(
        name: String,
        projectId: String,
        stateId: String,
    ): Task {
        verifyNoBlankInputs(name, projectId, stateId)
        verifyProjectAndStateExist(projectId, stateId)
        return createAndLogTask(name, projectId, stateId, getCurrentUserUseCase())
    }

    private suspend fun createAndLogTask(
        taskName: String,
        projectId: String,
        stateId: String,
        loggedInUser: User,
    ): Task {
        val taskId = Uuid.random().getCroppedId()
        val taskAuditLog = createAuditLog(taskId, taskName, loggedInUser)
        val newTask =
            Task(
                id = taskId,
                name = taskName,
                stateId = stateId,
                projectId = projectId,
                addedBy = loggedInUser.id,
                auditLogsIds = listOf(taskAuditLog.id),
            )

        auditLogRepository.createAuditLog(taskAuditLog)
        taskRepository.createTask(newTask)
        return newTask
    }

    private fun createAuditLog(
        taskId: String,
        name: String,
        loggedInUser: User,
    ): AuditLog {
        val currentTime = Clock.System.now()
        return AuditLog(
            id = Uuid.random().getCroppedId(),
            userId = loggedInUser.id,
            action = "user ${loggedInUser.username} created task $name at ${currentTime.formattedString()}",
            createdAt = currentTime,
            entityType = AuditLogEntityType.TASK,
            entityId = taskId,
            actionType = AuditLogActionType.CREATE,
        )
    }

    private suspend fun verifyProjectAndStateExist(
        projectId: String,
        stateId: String,
    ) {
        projectRepository.getProjectById(projectId)?.let { project ->
            if (project.states.none { it.id == stateId }) throw TaskStateNotFoundException()
        } ?: throw ProjectNotFoundException()
    }

    private fun verifyNoBlankInputs(
        name: String,
        projectId: String,
        stateId: String,
    ) {
        when {
            name.isBlank() -> throw BlankInputException()
            projectId.isBlank() -> throw BlankInputException()
            stateId.isBlank() -> throw BlankInputException()
        }
    }


}
