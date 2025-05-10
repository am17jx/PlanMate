package org.example.logic.useCase

import kotlinx.datetime.Clock
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.TaskNotChangedException
import org.example.logic.utils.TaskNotFoundException
import org.example.logic.utils.formattedString
import java.util.*

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository,
    private val currentUserUseCase: GetCurrentUserUseCase,
) {
    suspend operator fun invoke(
        taskId: String,
        updatedTask: Task,
    ): Task {
        val existingTask = getExistingTaskOrThrow(taskId)
        ensureTaskIsChanged(existingTask, updatedTask)
        return updateAndLogTask(existingTask, updatedTask, currentUserUseCase())
    }

    private suspend fun updateAndLogTask(
        oldTask: Task,
        updatedTask: Task,
        currentUser: User,
    ): Task {
        val taskAuditLog = logAudit(currentUser, updatedTask, oldTask)

        val taskLog = auditLogRepository.createAuditLog(taskAuditLog)

        taskRepository.updateTask(updatedTask)
        return updatedTask.copy(auditLogsIds = oldTask.auditLogsIds.plus(taskLog.id ?: ""))
    }

    private fun logAudit(
        user: User,
        oldTask: Task,
        newTask: Task,
    ): AuditLog {
        val currentTime = Clock.System.now()
        return AuditLog(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            action = "Updated task from stateId=${oldTask.stateId} to stateId=${newTask.stateId} at ${currentTime.formattedString()}",
            createdAt = currentTime,
            entityType = AuditLogEntityType.TASK,
            entityId = newTask.id,
            actionType = AuditLogActionType.UPDATE,
        )
    }

    private suspend fun getExistingTaskOrThrow(taskId: String): Task {
        return taskRepository.getTaskById(taskId) ?: throw TaskNotFoundException()
    }

    private fun ensureTaskIsChanged(
        oldTask: Task,
        newTask: Task,
    ) {
        if (oldTask == newTask) {
            throw TaskNotChangedException()
        }
    }
}
