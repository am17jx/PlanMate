package org.example.data.repository

import org.example.data.source.remote.contract.RemoteAuditLogDataSource
import org.example.logic.models.AuditLog
import org.example.logic.repositries.AuditLogRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AuditLogRepositoryImpl(
    private val remoteAuditLogDataSource: RemoteAuditLogDataSource
) : AuditLogRepository {
    override suspend fun createAuditLog(log: AuditLog): AuditLog {
        return remoteAuditLogDataSource.saveAuditLog(log)
    }

    override suspend fun deleteAuditLog(logId: Uuid) {
        return remoteAuditLogDataSource.deleteAuditLog(logId)
    }

    override suspend fun getEntityLogs(entityId: String, entityType: AuditLog.EntityType): List<AuditLog> {
        return remoteAuditLogDataSource.getEntityLogs(entityId, entityType)
    }

    override suspend fun getEntityLogByLogId(auditLogId: Uuid): AuditLog? {
        return remoteAuditLogDataSource.getEntityLogByLogId(auditLogId)
    }
}