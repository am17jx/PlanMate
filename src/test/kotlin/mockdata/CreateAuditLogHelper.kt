package mockdata

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType

fun createAuditLog(
    id: String = "",
    userId: String = "",
    action: String = "",
    timestamp: Long = 0L,
    entityType: AuditLogEntityType = AuditLogEntityType.TASK,
    entityId: String = "",
    actionType: AuditLogActionType = AuditLogActionType.CREATE
) = AuditLog(
    id = id,
    userId = userId,
    action = action,
    timestamp = timestamp,
    entityType = entityType,
    entityId = entityId,
    actionType = actionType
)
