package org.example.logic.repositries

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType

interface AuditLogRepository {
    suspend fun createAuditLog(log: AuditLog): AuditLog
    suspend fun deleteAuditLog(logId: String)
    suspend fun getEntityLogs(entityId: String, entityType: AuditLogEntityType): List<AuditLog>
    suspend fun getEntityLogByLogId(auditLogId:String): AuditLog?
}