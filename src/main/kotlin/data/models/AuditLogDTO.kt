package org.example.data.models

import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType

data class AuditLogDTO(
    val id: String,
    val userId: String,
    val action: String,
    val timestamp: Long,
    val entityType: AuditLogEntityType,
    val entityId: String,
    val actionType: AuditLogActionType
)
