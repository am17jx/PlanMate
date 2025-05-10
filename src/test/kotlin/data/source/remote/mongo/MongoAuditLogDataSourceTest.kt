package data.source.remote.mongo

import com.google.common.truth.Truth.assertThat
import com.mongodb.MongoClientException
import com.mongodb.MongoTimeoutException
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.source.remote.contract.RemoteAuditLogDataSource
import kotlinx.datetime.Clock
import org.example.data.source.remote.models.AuditLogDTO
import org.example.data.source.remote.mongo.MongoAuditLogDataSource
import org.example.data.source.remote.mongo.utils.mapper.toAuditLogDTO
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MongoAuditLogDataSourceTest {
    private lateinit var mongoClientCollection: MongoCollection<AuditLogDTO>
    private lateinit var remoteAuditLogDataSource: RemoteAuditLogDataSource
    private val currentTime = Clock.System.now()
    private val testAuditLogs =
        listOf(
            AuditLog(
                id = "1",
                userId = "1",
                action = "action 1",
                createdAt = currentTime,
                entityType = AuditLogEntityType.PROJECT,
                entityId = "123",
                actionType = AuditLogActionType.CREATE,
            ),
            AuditLog(
                id = "2",
                userId = "2",
                action = "action 2",
                createdAt = currentTime,
                entityType = AuditLogEntityType.TASK,
                entityId = "234",
                actionType = AuditLogActionType.UPDATE,
            ),
        )

    private val testAuditLogDTOs = testAuditLogs.map { it.toAuditLogDTO() }

    private val newAuditLog =
        AuditLog(
            id = "3",
            userId = "3",
            action = "action 3",
            createdAt = currentTime,
            entityType = AuditLogEntityType.PROJECT,
            entityId = "321",
            actionType = AuditLogActionType.DELETE,
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
            remoteAuditLogDataSource.getEntityLogs("1", AuditLogEntityType.PROJECT)

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
    fun `saveAuditLog should throw MongoClientException when happen incorrect configuration`() = runTest {

        coEvery { mongoClientCollection.insertOne(newAuditLogDTO, any()) } throws MongoClientException("Error")

        assertThrows<MongoClientException> { remoteAuditLogDataSource.saveAuditLog(newAuditLog) }

    }

    @Test
    fun `getEntityLogByLogId should return audit log when get audit log by Id from MongoDB`() =
        runTest {
            remoteAuditLogDataSource.getEntityLogByLogId("1")

            coVerify(exactly = 1) { mongoClientCollection.find(filter = any()) }
        }

    @Test
    fun `getEntityLogByLogId should throw MongoClientException when happen incorrect configuration`() = runTest {

        coEvery { mongoClientCollection.find(filter = any()) } throws MongoClientException("Error")

        assertThrows<MongoClientException> { remoteAuditLogDataSource.getEntityLogByLogId("1") }
    }

    @Test
    fun `deleteAuditLog should delete audit log when delete audit log from MongoDB`() =
        runTest {
            remoteAuditLogDataSource.deleteAuditLog("1")

            coVerify(exactly = 1) { mongoClientCollection.deleteOne(filter = any(), options = any()) }
        }

    @Test
    fun `deleteAuditLog should throw MongoTimeoutException when a connection or operation exceeds its time limit`() = runTest {

        coEvery { mongoClientCollection.deleteOne(filter = any(), options = any()) } throws MongoTimeoutException("Timeout")

        assertThrows<MongoTimeoutException> { remoteAuditLogDataSource.deleteAuditLog("1") }
    }

}
