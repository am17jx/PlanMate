package org.example.logic.useCase

import kotlinx.datetime.Clock
import org.example.logic.models.*
import org.example.logic.models.AuditLog.FieldChange.Companion.detectChanges
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.TaskNotChangedException
import org.example.logic.utils.TaskNotFoundException
import org.example.logic.utils.formattedString
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val createAuditLogUseCase: CreateAuditLogUseCase
) {
    suspend operator fun invoke(
        taskId: String,
        updatedTask: Task,
    ): Task {
        val existingTask = getExistingTaskOrThrow(taskId)
        ensureTaskIsChanged(existingTask, updatedTask)
        return updateAndLogTask(existingTask, updatedTask)
    }

    private suspend fun updateAndLogTask(
        oldTask: Task,
        updatedTask: Task,
    ): Task {
        val logsIds = updatedTask.detectChanges(oldTask).map { change ->
            createAuditLogUseCase.logUpdate(
                entityType = AuditLog.EntityType.TASK,
                entityId = oldTask.id,
                entityName = updatedTask.name,
                fieldChange = change
            ).id
        }
        return taskRepository.updateTask(updatedTask.copy(auditLogsIds = oldTask.auditLogsIds.plus(logsIds)))
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
