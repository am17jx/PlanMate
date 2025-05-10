package org.example.data.source.remote.contract

import org.example.logic.models.AuditLog
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RemoteAuditLogDataSource {
    suspend fun saveAuditLog(auditLog: AuditLog): AuditLog
    suspend fun deleteAuditLog(auditLogId: Uuid)
    suspend fun getEntityLogs(entityId: String, entityType: AuditLog.EntityType): List<AuditLog>
    suspend fun getEntityLogByLogId(auditLogId: Uuid): AuditLog?
}