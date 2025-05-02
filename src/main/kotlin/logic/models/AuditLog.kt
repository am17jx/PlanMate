package org.example.logic.models

data class AuditLog(
    val id: String,
    val userId: String,
    val action: String,
    val timestamp: Long,
    val entityType: AuditLogEntityType,
    val entityId: String,
    val actionType: AuditLogActionType
)
