package org.example.data.source.remote.contract

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType

interface RemoteAuditLogDataSource {
    suspend fun saveAuditLog(auditLog: AuditLog): AuditLog
    suspend fun deleteAuditLog(auditLogId: String)
    suspend fun getEntityLogs(entityId: String, entityType: AuditLogEntityType): List<AuditLog>
    suspend fun getEntityLogByLogId(auditLogId:String): AuditLog?
}