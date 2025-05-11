package org.example.logic.useCase

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLog.FieldChange.Companion.detectChanges
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.TaskNotChangedException
import org.example.logic.utils.TaskNotFoundException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val createAuditLogUseCase: CreateAuditLogUseCase,
) {
    suspend operator fun invoke(
        updatedTask: Task,
    ): Task {
        val existingTask = getExistingTaskOrThrow(updatedTask.id)
        ensureTaskIsChanged(existingTask, updatedTask)
        return taskRepository.updateTask(updatedTask).also {
            createLogs(
                oldTask = existingTask, updatedTask = updatedTask
            )
        }
    }

    private suspend fun createLogs(
        oldTask: Task,
        updatedTask: Task,
    ) {
        updatedTask.detectChanges(oldTask).map { change ->
            createAuditLogUseCase.logUpdate(
                entityType = AuditLog.EntityType.TASK,
                entityId = oldTask.id,
                entityName = updatedTask.name,
                fieldChange = change,
            ).id
        }
    }

    private suspend fun getExistingTaskOrThrow(taskId: Uuid): Task =
        taskRepository.getTaskById(taskId) ?: throw TaskNotFoundException()

    private fun ensureTaskIsChanged(
        oldTask: Task,
        newTask: Task,
    ) {
        if (oldTask == newTask) {
            throw TaskNotChangedException()
        }
    }
}
