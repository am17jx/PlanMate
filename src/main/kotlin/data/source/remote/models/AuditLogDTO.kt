package org.example.data.source.remote.models

import org.example.logic.models.AuditLog

data class AuditLogDTO(
    val id: String,
    val createdAt: Long,
    val userId: String,
    val userName: String,
    val entityId: String,
    val entityName: String,
    val entityType: String,
    val actionType: String,
    val fieldChange: FieldChangeDto?
) {
    data class FieldChangeDto(
        val fieldName: String,
        val oldValue: String,
        val newValue: String,
    )
}
