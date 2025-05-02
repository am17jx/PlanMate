package org.example.data.repository

import org.example.data.source.local.cotract.LocalAuditLogDataSource
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository

class AuditLogRepositoryImpl(
    private val localAuditLogDataSource: LocalAuditLogDataSource
) : AuditLogRepository {
    override fun createAuditLog(log: AuditLog): AuditLog {
        return localAuditLogDataSource.saveAuditLog(log)
    }

    override fun deleteAuditLog(logId: String) {
        return localAuditLogDataSource.deleteAuditLog(logId)
    }

    override fun getEntityLogs(entityId: String, entityType: AuditLogEntityType): List<AuditLog> {
        return localAuditLogDataSource.getEntityLogs(entityId,entityType)
    }

    override fun getEntityLogByLogId(auditLogId: String): AuditLog? {
        return localAuditLogDataSource.getEntityLogByLogId(auditLogId)
    }
}