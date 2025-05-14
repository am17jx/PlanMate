package org.example.data.repository.sources.remote

import org.example.logic.models.AuditLog
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RemoteAuditLogDataSource {
    suspend fun saveAuditLog(auditLog: AuditLog): AuditLog

    suspend fun deleteAuditLog(auditLogId: Uuid)

    suspend fun getEntityLogs(
        entityId: Uuid,
        entityType: AuditLog.EntityType,
    ): List<AuditLog>

    suspend fun getEntityLogByLogId(auditLogId: Uuid): AuditLog?
}
