package org.example.data.repository.sources.local

import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType

interface LocalAuditLogDataSource {
    fun saveAuditLog(auditLog: AuditLog):AuditLog
    fun deleteAuditLog(auditLogId: String)
    fun getEntityLogs(entityId: String, entityType: AuditLogEntityType): List<AuditLog>
    fun getEntityLogByLogId(auditLogId:String): AuditLog?
}