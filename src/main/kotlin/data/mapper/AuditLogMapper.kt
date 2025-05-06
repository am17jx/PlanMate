package org.example.data.mapper

import org.example.data.models.AuditLogDTO
import org.example.logic.models.AuditLog

fun AuditLogDTO.toAuditLog(): AuditLog {
    return AuditLog(
        id = id,
        userId = userId,
        action = action,
        timestamp = timestamp,
        entityType = entityType,
        entityId = entityId,
        actionType = actionType
    )
}

fun AuditLog.toAuditLogDTO(): AuditLogDTO {
    return AuditLogDTO(
        id = id,
        userId = userId,
        action = action,
        timestamp = timestamp,
        entityType = entityType,
        entityId = entityId,
        actionType = actionType
    )
}