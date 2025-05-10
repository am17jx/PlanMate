package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.remote.contract.RemoteAuditLogDataSource
import org.example.data.source.remote.models.AuditLogDTO
import org.example.data.source.remote.models.AuditLogDTO
import org.example.data.source.remote.mongo.utils.executeMongoOperation
import org.example.data.source.remote.mongo.utils.mapper.toAuditLog
import org.example.data.source.remote.mongo.utils.mapper.toAuditLogDTO
import org.example.data.repository.sources.remote.RemoteAuditLogDataSource
import org.example.data.utils.Constants.ENTITY_ID
import org.example.data.utils.Constants.ENTITY_TYPE
import org.example.data.utils.Constants.ID
import org.example.logic.models.AuditLog
import org.example.logic.utils.CreationItemFailedException
import org.example.logic.utils.DeleteItemFailedException
import org.example.logic.utils.GetItemByIdFailedException
import org.example.logic.utils.GetItemsFailedException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.example.logic.models.AuditLogEntityType

@OptIn(ExperimentalUuidApi::class)
class MongoAuditLogDataSource(private val auditLogCollection: MongoCollection<AuditLogDTO>) : RemoteAuditLogDataSource {


    override suspend fun saveAuditLog(auditLog: AuditLog): AuditLog {
        return executeMongoOperation {
            auditLogCollection.insertOne(auditLog.toAuditLogDTO())
            auditLog
        }
    }

    override suspend fun deleteAuditLog(auditLogId: Uuid) {
        return executeMongoOperation { auditLogCollection.deleteOne(Filters.eq(ID, auditLogId.toHexString())) }
    }


    override suspend fun getEntityLogs(entityId: String, entityType: AuditLog.EntityType): List<AuditLog> {
        return executeMongoOperation {
             auditLogCollection.find(
                    Filters.and(
                        Filters.eq(ENTITY_ID, entityId),
                        Filters.eq(ENTITY_TYPE, entityType.name)
                    )
                ).toList().map { it.toAuditLog() }
        }
    }

    override suspend fun getEntityLogByLogId(auditLogId: Uuid): AuditLog? {
        return executeMongoOperation {
            auditLogCollection.find(Filters.eq(ID, auditLogId.toHexString())).firstOrNull()?.toAuditLog()
        }
    }
}