package data.source.remote.mongo

import com.google.common.truth.Truth.assertThat
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.data.mapper.toAuditLogDTO
import org.example.data.models.AuditLogDTO
import org.example.data.source.remote.contract.RemoteAuditLogDataSource
import org.example.data.source.remote.mongo.MongoAuditLogDataSource
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.utils.CreationItemFailedException
import org.example.logic.utils.DeleteItemFailedException
import org.example.logic.utils.GetItemByIdFailedException
import org.example.logic.utils.GetItemsFailedException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MongoAuditLogDataSourceTest {
    private lateinit var mongoClientCollection: MongoCollection<AuditLogDTO>
    private lateinit var remoteAuditLogDataSource: RemoteAuditLogDataSource

    private val testAuditLogs = listOf(
        AuditLog(
            id = "1",
            userId = "1",
            action = "action 1",
            timestamp = 11,
            entityType = AuditLogEntityType.PROJECT,
            entityId = "123",
            actionType = AuditLogActionType.CREATE

        ),
        AuditLog(
            id = "2",
            userId = "2",
            action = "action 2",
            timestamp = 11,
            entityType = AuditLogEntityType.TASK,
            entityId = "234",
            actionType = AuditLogActionType.UPDATE
        )
    )

    private val testAuditLogDTOs = testAuditLogs.map { it.toAuditLogDTO() }

    private val newAuditLog = AuditLog(
        id = "3",
        userId = "3",
        action = "action 3",
        timestamp = 11,
        entityType = AuditLogEntityType.PROJECT,
        entityId = "321",
        actionType = AuditLogActionType.DELETE
    )
    private val newAuditLogDTO = newAuditLog.toAuditLogDTO()


    @BeforeEach
    fun setUp() {
        mongoClientCollection = mockk(relaxed = true)
        remoteAuditLogDataSource = MongoAuditLogDataSource(mongoClientCollection)
    }


    @Test
    fun `getEntityLogs should return list of AuditLog when  try to get audit logs from MongoDB`() = runTest {

        remoteAuditLogDataSource.getEntityLogs("1", AuditLogEntityType.PROJECT)

        coVerify(exactly = 1) { mongoClientCollection.find(filter = any()) }
    }

    @Test
    fun `getEntityLogs should throw GetItemsFailedException when try to get projects fails in MongoDB`() = runTest {

        coEvery { mongoClientCollection.find(filter = any()) } throws Exception()

        assertThrows<GetItemsFailedException> { remoteAuditLogDataSource.getEntityLogs("1", AuditLogEntityType.PROJECT) }
    }

    @Test
    fun `saveAuditLog should return audit log that created when create audit log at MongoDB`() = runTest {

        val createAuditLog = remoteAuditLogDataSource.saveAuditLog(newAuditLog)

        coVerify(exactly = 1) { mongoClientCollection.insertOne(newAuditLogDTO, any()) }
        assertThat(createAuditLog).isEqualTo(newAuditLog)
    }

    @Test
    fun `saveAuditLog should throw CreationItemFailedException when create audit log fails in MongoDB`() = runTest {

        coEvery { mongoClientCollection.insertOne(newAuditLogDTO, any()) } throws Exception()

        assertThrows<CreationItemFailedException> { remoteAuditLogDataSource.saveAuditLog(newAuditLog) }

    }

    @Test
    fun `getEntityLogByLogId should return audit log when get audit log by Id from MongoDB`() = runTest {

        remoteAuditLogDataSource.getEntityLogByLogId("1")

        coVerify(exactly = 1) { mongoClientCollection.find(filter = any()) }
    }


    @Test
    fun `getEntityLogByLogId should throw GetItemByIdFailedException when get audit log by ID fails in MongoDB`() = runTest {

        coEvery { mongoClientCollection.find(filter = any()) } throws Exception()

        assertThrows<GetItemByIdFailedException> { remoteAuditLogDataSource.getEntityLogByLogId("1") }
    }


    @Test
    fun `deleteAuditLog should delete audit log when delete audit log from MongoDB`() = runTest {

        remoteAuditLogDataSource.deleteAuditLog("1")

        coVerify(exactly = 1) { mongoClientCollection.deleteOne(filter = any(), options = any()) }
    }

    @Test
    fun `deleteAuditLog should throw DeleteItemFailedException when delete audit log fails in MongoDB`() = runTest {

        coEvery { mongoClientCollection.deleteOne(filter = any(), options = any()) } throws Exception()

        assertThrows<DeleteItemFailedException> { remoteAuditLogDataSource.deleteAuditLog("1") }
    }

}