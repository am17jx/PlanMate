package org.example.logic.useCase

import kotlinx.datetime.Clock
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.TaskDeletionFailedException
import org.example.logic.utils.formattedString
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository,
    private val userUseCase: GetCurrentUserUseCase,
) {
    suspend operator fun invoke(taskId: String) {
        try {
            auditLogRepository.createAuditLog(saveAuditLog(taskId))

            taskRepository.deleteTask(taskId)
        } catch (e: Exception) {
            throw TaskDeletionFailedException()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun saveAuditLog(taskId: String): AuditLog {
        val user = userUseCase()
        val currentTime = Clock.System.now()
        val auditLog =
            AuditLog(
                id = Uuid.random().getCroppedId(),
                userId = user.id,
                action = "${user.username} deleted task with id $taskId at ${currentTime.formattedString()}",
                createdAt = currentTime,
                entityType = AuditLogEntityType.TASK,
                entityId = taskId,
                actionType = AuditLogActionType.DELETE,
            )
        return auditLog
    }
}
