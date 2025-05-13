package data.source.remote.mongo

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoClientException
import com.mongodb.MongoException
import com.mongodb.MongoTimeoutException
import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.example.data.repository.sources.remote.RemoteAuditLogDataSource
import org.example.data.source.remote.models.AuditLogDTO
import org.example.data.source.remote.mongo.MongoAuditLogDataSource
import org.example.data.source.remote.mongo.utils.mapper.toAuditLogDTO
import org.example.data.utils.Constants.ENTITY_ID
import org.example.data.utils.Constants.ENTITY_TYPE
import org.example.data.utils.Constants.ID
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLog.ActionType
import org.example.logic.models.AuditLog.EntityType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
class MongoAuditLogDataSourceTest {
    private lateinit var mongoClientCollection: MongoCollection<AuditLogDTO>
    private lateinit var remoteAuditLogDataSource: RemoteAuditLogDataSource

    private val currentTime = Clock.System.now()
    private val testAuditLogs =
        listOf(
            AuditLog(
                id = Uuid.random(),
                userId = Uuid.random(),
                userName = "User1",
                createdAt = currentTime,
                entityType = EntityType.PROJECT,
                entityId = Uuid.random(),
                entityName = "Entity1",
                actionType = ActionType.CREATE,
            ),
            AuditLog(
                id = Uuid.random(),
                userId = Uuid.random(),
                userName = "User2",
                createdAt = currentTime,
                entityType = EntityType.TASK,
                entityId = Uuid.random(),
                entityName = "Entity2",
                actionType = ActionType.UPDATE,
            ),
        )

    private val testAuditLogDTOs = testAuditLogs.map { it.toAuditLogDTO() }

    private val newAuditLog =
        AuditLog(
            id = Uuid.random(),
            userId = Uuid.random(),
            userName = "User3",
            createdAt = currentTime,
            entityType = EntityType.PROJECT,
            entityId = Uuid.random(),
            entityName = "Entity3",
            actionType = ActionType.DELETE,
        )
    private val newAuditLogDTO = newAuditLog.toAuditLogDTO()

    @BeforeEach
    fun setUp() {
        mongoClientCollection = mockk(relaxed = true)
        remoteAuditLogDataSource = MongoAuditLogDataSource(mongoClientCollection)
    }

    @Test
    fun `getEntityLogs should return list of AuditLog when  try to get audit logs from MongoDB`() =
        runTest {
            remoteAuditLogDataSource.getEntityLogs(Uuid.random(), EntityType.PROJECT)

            coVerify(exactly = 1) { mongoClientCollection.find(filter = any()) }
        }

    @Test
    fun `saveAuditLog should return audit log that created when create audit log at MongoDB`() =
        runTest {
            val createAuditLog = remoteAuditLogDataSource.saveAuditLog(newAuditLog)

            coVerify(exactly = 1) { mongoClientCollection.insertOne(newAuditLogDTO, any()) }
            assertThat(createAuditLog).isEqualTo(newAuditLog)
        }

    @Test
    fun `saveAuditLog should throw MongoClientException when happen incorrect configuration`() =
        runTest {
            coEvery { mongoClientCollection.insertOne(newAuditLogDTO, any()) } throws MongoClientException("Error")

            assertThrows<MongoClientException> { remoteAuditLogDataSource.saveAuditLog(newAuditLog) }
        }

    @Test
    fun `getEntityLogByLogId should return audit log when get audit log by Id from MongoDB`() =
        runTest {
            remoteAuditLogDataSource.getEntityLogByLogId(Uuid.random())

            coVerify(exactly = 1) { mongoClientCollection.find(filter = any()) }
        }

    @Test
    fun `getEntityLogByLogId should throw MongoClientException when happen incorrect configuration`() =
        runTest {
            coEvery { mongoClientCollection.find(filter = any()) } throws MongoClientException("Error")

            assertThrows<MongoClientException> { remoteAuditLogDataSource.getEntityLogByLogId(Uuid.random()) }
        }

    @Test
    fun `deleteAuditLog should delete audit log when delete audit log from MongoDB`() =
        runTest {
            remoteAuditLogDataSource.deleteAuditLog(Uuid.random())

            coVerify(exactly = 1) { mongoClientCollection.deleteOne(filter = any(), options = any()) }
        }

    @Test
    fun `deleteAuditLog should throw MongoTimeoutException when a connection or operation exceeds its time limit`() =
        runTest {
            coEvery { mongoClientCollection.deleteOne(filter = any(), options = any()) } throws MongoTimeoutException("Timeout")

            assertThrows<MongoTimeoutException> { remoteAuditLogDataSource.deleteAuditLog(Uuid.random()) }
        }


    @Test
    fun `getEntityLogByLogId should return null when log doesn't exist`() = runTest {
      
        val auditLogId = Uuid.random()
        val emptyFindFlow: FindFlow<AuditLogDTO> = mockk(relaxed = true)
        coEvery { emptyFindFlow.collect(any()) } just Runs

        coEvery {
            mongoClientCollection.find(
                Filters.eq(ID, auditLogId.toHexString())
            )
        } returns emptyFindFlow

        
        val result = remoteAuditLogDataSource.getEntityLogByLogId(auditLogId)

       
        assertNull(result)
        coVerify(exactly = 1) {
            mongoClientCollection.find(
                Filters.eq(ID, auditLogId.toHexString())
            )
        }
    }

    @Test
    fun `getEntityLogs should throws MongoClientException when `() = runTest {
       
        val entityId = Uuid.random()
        val entityType = EntityType.PROJECT
        coEvery { mongoClientCollection.find(filter = any()) } throws MongoClientException("Error")

      
        assertThrows<MongoClientException> {
            remoteAuditLogDataSource.getEntityLogs(entityId, entityType)
        }
    }

    @Test
    fun `getEntityLogByLogId should throw MongoDB exceptions`() = runTest {
       
        val auditLogId = Uuid.random()
        coEvery { mongoClientCollection.find(filter = any()) } throws MongoClientException("Error")

       
        assertThrows<MongoException> {
            remoteAuditLogDataSource.getEntityLogByLogId(auditLogId)
        }
    }

    @Test
    fun `getEntityLogByLogId should return audit log when exists`() = runTest {
       
        val auditLogId = Uuid.random()
        val findFlow: FindFlow<AuditLogDTO> = mockk(relaxed = true)
        coEvery { findFlow.collect(any()) } coAnswers {
            val collector = firstArg<FlowCollector<AuditLogDTO>>()
            collector.emit(newAuditLogDTO)
        }

        coEvery {
            mongoClientCollection.find(
                Filters.eq(ID, auditLogId.toHexString())
            )
        } returns findFlow

        
        val result = remoteAuditLogDataSource.getEntityLogByLogId(auditLogId)

       
        assertNotNull(result)
        assertEquals(newAuditLogDTO.id, result?.id?.toHexString())
        coVerify(exactly = 1) {
            mongoClientCollection.find(
                Filters.eq(ID, auditLogId.toHexString())
            )
        }
    }
    @Test
    fun `getEntityLogs should return empty list when no logs exist`() = runTest {
       
        val entityId = Uuid.random()
        val entityType = EntityType.PROJECT
        val emptyFindFlow: FindFlow<AuditLogDTO> = mockk(relaxed = true)
        coEvery { emptyFindFlow.collect(any()) } just Runs

        coEvery {
            mongoClientCollection.find(
                Filters.and(
                    Filters.eq(ENTITY_ID, entityId.toHexString()),
                    Filters.eq(ENTITY_TYPE, entityType.name)
                )
            )
        } returns emptyFindFlow

        
        val result = remoteAuditLogDataSource.getEntityLogs(entityId, entityType)

       
        assertEquals(emptyList<AuditLogDTO>(), result)
        coVerify(exactly = 1) {
            mongoClientCollection.find(
                Filters.and(
                    Filters.eq(ENTITY_ID, entityId.toHexString()),
                    Filters.eq(ENTITY_TYPE, entityType.name)
                )
            )
        }
    }

    @Test
    fun `getEntityLogs should return list of audit logs`() = runTest {
       
        val entityId = Uuid.random()
        val entityType = EntityType.PROJECT
        val findFlow: FindFlow<AuditLogDTO> = mockk(relaxed = true)
        coEvery { findFlow.collect(any()) } coAnswers {
            val collector = firstArg<FlowCollector<AuditLogDTO>>()
            collector.emit(newAuditLogDTO)
        }

        coEvery {
            mongoClientCollection.find(
                Filters.and(
                    Filters.eq(ENTITY_ID, entityId.toHexString()),
                    Filters.eq(ENTITY_TYPE, entityType.name)
                )
            )
        } returns findFlow

        
        val result = remoteAuditLogDataSource.getEntityLogs(entityId, entityType)

       
        assertEquals(1, result.size)
        assertEquals(newAuditLogDTO.id, result[0].id.toHexString())
        coVerify(exactly = 1) {
            mongoClientCollection.find(
                Filters.and(
                    Filters.eq(ENTITY_ID, entityId.toHexString()),
                    Filters.eq(ENTITY_TYPE, entityType.name)
                )
            )
        }
    }

}

