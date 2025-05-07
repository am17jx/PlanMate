package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.AuditLogDTO
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType

fun AuditLogDTO.toAuditLog(): AuditLog {
    return AuditLog(
        id = id,
        userId = userId,
        action = action,
        timestamp = timestamp,
        entityType = AuditLogEntityType.valueOf(entityType),
        entityId = entityId,
        actionType = AuditLogActionType.valueOf(actionType)
    )
}

fun AuditLog.toAuditLogDTO(): AuditLogDTO {
    return AuditLogDTO(
        id = id,
        userId = userId,
        action = action,
        timestamp = timestamp,
        entityType = entityType.name,
        entityId = entityId,
        actionType = actionType.name
    )
}