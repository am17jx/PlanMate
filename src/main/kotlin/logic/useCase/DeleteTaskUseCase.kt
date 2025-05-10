package org.example.logic.useCase

import org.example.logic.models.AuditLog
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.TaskDeletionFailedException
import org.example.logic.utils.formattedString
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val createAuditLogUseCase: CreateAuditLogUseCase,
) {
    suspend operator fun invoke(taskId: String) {
        getTaskByIdUseCase(taskId).let { task ->
            createAuditLogUseCase.logDeletion(
                entityType = AuditLog.EntityType.PROJECT, entityId = task.id, entityName = task.name
            )
            taskRepository.deleteTask(taskId)
        } catch (e: Exception) {
            throw TaskDeletionFailedException()
        }
    }
}
