package mockdata

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType

fun createAuditLog(
    id: String = "",
    userId: String = "",
    action: String = "",
    timestamp: Long = 0,
    entityType: AuditLogEntityType = AuditLogEntityType.PROJECT,
    entityId: String = "",
    actionType: AuditLogActionType = AuditLogActionType.UPDATE
) = AuditLog(
    id = id,
    userId = userId,
    action = action,
    timestamp = timestamp,
    entityType = entityType,
    entityId = entityId,
    actionType = actionType
)