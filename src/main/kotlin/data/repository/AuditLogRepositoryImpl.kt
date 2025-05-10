package org.example.data.repository

import org.example.data.repository.sources.remote.RemoteAuditLogDataSource
import org.example.data.repository.mapper.mapExceptionsToDomainException
import org.example.logic.models.AuditLog
import org.example.logic.repositries.AuditLogRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.example.logic.utils.AuditLogCreationFailedException
import org.example.logic.utils.AuditLogDeletionFailedException
import org.example.logic.utils.AuditLogNotFoundException

@OptIn(ExperimentalUuidApi::class)
class AuditLogRepositoryImpl(
    private val remoteAuditLogDataSource: RemoteAuditLogDataSource,
) : AuditLogRepository {
    override suspend fun createAuditLog(log: AuditLog): AuditLog {
        return mapExceptionsToDomainException(AuditLogCreationFailedException()) {
            remoteAuditLogDataSource.saveAuditLog(log)
        }
    }

    override suspend fun deleteAuditLog(logId: Uuid) {
        return mapExceptionsToDomainException(AuditLogDeletionFailedException()) {
             remoteAuditLogDataSource.deleteAuditLog(logId)
        }
    }

    override suspend fun getEntityLogs(entityId: String, entityType: AuditLog.EntityType): List<AuditLog> {
        return mapExceptionsToDomainException(AuditLogNotFoundException()) {
            remoteAuditLogDataSource.getEntityLogs(entityId, entityType)
        }
    }

    override suspend fun getEntityLogByLogId(auditLogId: Uuid): AuditLog? {
        return mapExceptionsToDomainException(AuditLogNotFoundException()) {
             remoteAuditLogDataSource.getEntityLogByLogId(auditLogId)
        }
    }
}