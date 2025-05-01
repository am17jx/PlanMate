package org.example.data.utils.mapper

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.models.Task

fun AuditLog.toCsvRow(): String =
    listOf(id, userId, action, timestamp.toString(), entityType.name, entityId, actionType.name)
        .joinToString(",")


fun List<AuditLog>.toCsvRows(): List<String> = this.map { it.toCsvRow() }


fun List<String>.toAuditLogs(): List<AuditLog> {
    if (this.isEmpty()) return emptyList()

    return this.filter { it.isNotBlank() }
        .map { line ->
            line
                .split(",")
                .map { it.trim() }
                .takeIf { it.size == 7 }
                ?.let { parts ->
                    AuditLog(
                        id = parts[0],
                        userId = parts[1],
                        action = parts[2],
                        timestamp = parts[3].toLongOrNull()
                            ?: throw IllegalArgumentException("Invalid timestamp: ${parts[3]}"),
                        entityType = AuditLogEntityType.valueOf(parts[4]),
                        entityId = parts[5],
                        actionType = AuditLogActionType.valueOf(parts[6])
                    )
                } ?: throw IllegalArgumentException("CSV line doesn't have enough fields: $line")
        }
}