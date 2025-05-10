package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.remote.mongo.utils.mapper.toAuditLog
import org.example.data.source.remote.mongo.utils.mapper.toAuditLogDTO
import org.example.data.source.remote.models.AuditLogDTO
import org.example.data.repository.sources.remote.RemoteAuditLogDataSource
import org.example.data.utils.Constants.ENTITY_ID
import org.example.data.utils.Constants.ENTITY_TYPE
import org.example.data.utils.Constants.ID
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType
import org.example.logic.utils.CreationItemFailedException
import org.example.logic.utils.DeleteItemFailedException
import org.example.logic.utils.GetItemByIdFailedException
import org.example.logic.utils.GetItemsFailedException

class MongoAuditLogDataSource(private val auditLogCollection: MongoCollection<AuditLogDTO>): RemoteAuditLogDataSource {


    override suspend fun saveAuditLog(auditLog: AuditLog): AuditLog {

        try {
            auditLogCollection.insertOne(auditLog.toAuditLogDTO())
            return auditLog
        } catch (e: Exception) {
            throw CreationItemFailedException("audit log creation failed ${e.message}")
        }

    }

    override suspend fun deleteAuditLog(auditLogId: String) {

        try {
            auditLogCollection.deleteOne(Filters.eq(ID, auditLogId))
        } catch (e: Exception) {
            throw DeleteItemFailedException("audit log delete failed ${e.message}")
        }

    }

    override suspend fun getEntityLogs(entityId: String, entityType: AuditLogEntityType): List<AuditLog> {

        try {
            return auditLogCollection
                .find(Filters.and(Filters.eq(ENTITY_ID, entityId), Filters.eq(ENTITY_TYPE, entityType)))
                .toList().map { it.toAuditLog() }
        } catch (e: Exception) {
            throw GetItemsFailedException("get audit logs failed ${e.message}")
        }

    }

    override suspend fun getEntityLogByLogId(auditLogId: String): AuditLog? {

        try {
            return auditLogCollection.find(Filters.eq(ID, auditLogId)).firstOrNull()?.toAuditLog()
        } catch (e: Exception) {
            throw GetItemByIdFailedException("get audit log by id failed ${e.message}")
        }

    }


}