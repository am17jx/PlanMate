package org.example.data.repository

import org.example.data.source.local.contract.LocalAuditLogDataSource
import org.example.data.source.remote.contract.RemoteAuditLogDataSource
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository

class AuditLogRepositoryImpl(
    private val remoteAuditLogDataSource: RemoteAuditLogDataSource
) : AuditLogRepository {
    override suspend fun createAuditLog(log: AuditLog): AuditLog {
        return remoteAuditLogDataSource.saveAuditLog(log)
    }

    override suspend fun deleteAuditLog(logId: String) {
        return remoteAuditLogDataSource.deleteAuditLog(logId)
    }

    override suspend fun getEntityLogs(entityId: String, entityType: AuditLogEntityType): List<AuditLog> {
        return remoteAuditLogDataSource.getEntityLogs(entityId,entityType)
    }

    override suspend fun getEntityLogByLogId(auditLogId: String): AuditLog? {
        return remoteAuditLogDataSource.getEntityLogByLogId(auditLogId)
    }
}