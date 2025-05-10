package org.example.data.repository.sources.local

import org.example.logic.models.AuditLog
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface LocalAuditLogDataSource {
    fun saveAuditLog(auditLog: AuditLog): AuditLog
    fun deleteAuditLog(auditLogId: Uuid)
    fun getEntityLogs(entityId: Uuid, entityType: AuditLog.EntityType): List<AuditLog>
    fun getEntityLogByLogId(auditLogId: Uuid): AuditLog?
}