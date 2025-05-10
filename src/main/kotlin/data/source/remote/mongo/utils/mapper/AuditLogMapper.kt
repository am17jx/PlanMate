package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.AuditLogDTO
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.utils.toInstant

fun AuditLogDTO.toAuditLog(): AuditLog =
    AuditLog(
        id = id,
        userId = userId,
        action = action,
        createdAt = createdAt.toInstant(),
        entityType = AuditLogEntityType.valueOf(entityType),
        entityId = entityId,
        actionType = AuditLogActionType.valueOf(actionType),
    )

fun AuditLog.toAuditLogDTO(): AuditLogDTO =
    AuditLogDTO(
        id = id,
        userId = userId,
        action = action,
        createdAt = createdAt.toEpochMilliseconds(),
        entityType = entityType.name,
        entityId = entityId,
        actionType = actionType.name,
    )
