package mockdata

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType

fun createAuditLog(
    id: String = "",
    userId: String = "",
    action: String = "",
    createdAt: Instant = Clock.System.now(),
    entityType: AuditLogEntityType = AuditLogEntityType.TASK,
    entityId: String = "",
    actionType: AuditLogActionType = AuditLogActionType.CREATE,
) = AuditLog(
    id = id,
    userId = userId,
    action = action,
    createdAt = createdAt,
    entityType = entityType,
    entityId = entityId,
    actionType = actionType,
)
