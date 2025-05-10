package org.example.data.source.remote.models

data class AuditLogDTO(
    val id: String,
    val userId: String,
    val action: String,
    val createdAt: Long,
    val entityType: String,
    val entityId: String,
    val actionType: String,
)
