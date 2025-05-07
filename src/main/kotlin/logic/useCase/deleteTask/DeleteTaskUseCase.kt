package org.example.logic.useCase.deleteTask

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionalCommand
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.utils.UnableToDeleteTaskException
import org.example.logic.utils.formattedString
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository,
    private val userUseCase: GetCurrentUserUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
) {
    suspend operator fun invoke(taskId: String) {
        val auditLog = saveAuditLog(taskId)
        val auditCommand = CreateAuditLogCommand(auditLogRepository, auditLog)
        val deleteTasksCommand = DeleteTaskCommand(taskRepository, getTaskByIdUseCase(taskId))
        TransactionalCommand(listOf(auditCommand, deleteTasksCommand), UnableToDeleteTaskException("Cannot delete task"))
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun saveAuditLog(taskId: String): AuditLog {
        val user = userUseCase()
        val timestampNow = Clock.System.now()
        val auditLog = AuditLog(
            id = Uuid.random().getCroppedId(),
            userId = user.id,
            action = "${user.username} deleted task with id $taskId at ${timestampNow.formattedString()}",
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.TASK,
            entityId = taskId,
            actionType = AuditLogActionType.DELETE
        )
        return auditLog
    }
}