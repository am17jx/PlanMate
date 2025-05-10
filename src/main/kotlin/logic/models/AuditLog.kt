package org.example.logic.models

import kotlinx.datetime.Instant

data class AuditLog(
    val id: String,
    val userId: String,
    val action: String,
    val createdAt: Instant,
    val entityType: AuditLogEntityType,
    val entityId: String,
    val actionType: AuditLogActionType,
)
