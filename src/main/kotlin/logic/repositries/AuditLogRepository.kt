package org.example.logic.repositries

import org.example.logic.models.AuditLog
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface AuditLogRepository {
    suspend fun createAuditLog(log: AuditLog): AuditLog
    suspend fun deleteAuditLog(logId: Uuid)
    suspend fun getEntityLogs(entityId: String, entityType: AuditLog.EntityType): List<AuditLog>
    suspend fun getEntityLogByLogId(auditLogId: Uuid): AuditLog?
}