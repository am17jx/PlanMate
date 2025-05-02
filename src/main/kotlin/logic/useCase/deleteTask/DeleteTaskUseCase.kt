package org.example.logic.useCase.deleteTask

import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionCommands
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.utils.UnableToDeleteTaskException
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository,
    private val userUseCase: GetCurrentUserUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
) {
    operator fun invoke(taskId: String) {
        val auditLog = saveAuditLog(taskId)
        val auditCommand = CreateAuditLogCommand(auditLogRepository, auditLog)
        val deleteTasksCommand = DeleteTaskCommand(taskRepository, getTaskByIdUseCase(taskId))
        TransactionCommands(listOf(auditCommand, deleteTasksCommand), UnableToDeleteTaskException("Cannot delete task"))

    }

    @OptIn(ExperimentalUuidApi::class)
    private fun saveAuditLog(taskId: String): AuditLog {
        val auditLog = AuditLog(
            id = Uuid.random().getCroppedId(),
            userId = userUseCase().id,
            action = "${userUseCase().username} deleted task with id $taskId",
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.TASK,
            entityId = taskId,
            actionType = AuditLogActionType.DELETE
        )
        return auditLog
    }
}