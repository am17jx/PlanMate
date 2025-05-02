package org.example.logic.useCase

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskNotFoundException

class GetEntityAuditLogsUseCase(
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(entityId: String, entityType: AuditLogEntityType): List<AuditLog> {
        verifyEntityIdNotBlank(entityId)
        return auditLogRepository
            .getEntityLogs(entityId, entityType)
            .takeIf {
                it.isNotEmpty()
            }?: throw getEntityNotFoundException(entityType)
    }

    private fun getEntityNotFoundException(entityType: AuditLogEntityType) = when (entityType) {
        AuditLogEntityType.TASK -> TaskNotFoundException(TASK_NOT_FOUND_ERROR_MESSAGE)
        AuditLogEntityType.PROJECT -> ProjectNotFoundException(PROJECT_NOT_FOUND_ERROR_MESSAGE)
    }

    private fun verifyEntityIdNotBlank(entityId: String) {
        if (entityId.isBlank()) throw BlankInputException(BLANK_ENTITY_ID_ERROR_MESSAGE)
    }

    companion object{
        const val BLANK_ENTITY_ID_ERROR_MESSAGE = "Entity id cannot be blank"
        const val TASK_NOT_FOUND_ERROR_MESSAGE = "No task found with this id"
        const val PROJECT_NOT_FOUND_ERROR_MESSAGE = "No project found with this id"
    }
}