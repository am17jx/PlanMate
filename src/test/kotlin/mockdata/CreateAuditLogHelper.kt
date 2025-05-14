package mockdata

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import org.example.logic.models.AuditLog
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun createAuditLog(
    id: Uuid = Uuid.random(),
    userId: Uuid = Uuid.random(),
    userName: String = "",
    createdAt: Instant = Clock.System.now(),
    entityType: AuditLog.EntityType = AuditLog.EntityType.TASK,
    entityName: String = "",
    entityId: Uuid = Uuid.random(),
    actionType: AuditLog.ActionType = AuditLog.ActionType.CREATE,
    fieldChange: AuditLog.FieldChange? = null
) = AuditLog(
    id = id,
    userId = userId,
    userName = userName,
    createdAt = createdAt,
    entityType = entityType,
    entityId = entityId,
    entityName = entityName,
    actionType = actionType,
    fieldChange = fieldChange
)
