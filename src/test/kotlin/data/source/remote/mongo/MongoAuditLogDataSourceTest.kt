package data.source.remote.mongo

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoClientException
import com.mongodb.MongoTimeoutException
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.example.data.repository.sources.remote.RemoteAuditLogDataSource
import org.example.data.source.remote.models.AuditLogDTO
import org.example.data.source.remote.mongo.MongoAuditLogDataSource
import org.example.data.source.remote.mongo.utils.mapper.toAuditLogDTO
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLog.ActionType
import org.example.logic.models.AuditLog.EntityType
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
            remoteAuditLogDataSource.getEntityLogs(Uuid.random(), AuditLog.EntityType.PROJECT)

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
}
