package org.example.logic.repositries

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType

interface AuditLogRepository {
    fun createAuditLog(log: AuditLog): AuditLog?
    fun deleteAuditLog(logId: String)
    fun getEntityLogs(entityId: String, entityType: AuditLogEntityType): List<AuditLog>
}