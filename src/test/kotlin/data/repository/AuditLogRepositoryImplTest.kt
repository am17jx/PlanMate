package org.example.data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertThrows
import org.example.data.repository.sources.remote.RemoteAuditLogDataSource
import org.example.logic.models.AuditLog
import org.example.logic.utils.AuditLogCreationFailedException
import org.example.logic.utils.AuditLogDeletionFailedException
import org.example.logic.utils.AuditLogNotFoundException
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlinx.datetime.Instant

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalUuidApi::class)
class AuditLogRepositoryImplTest {

    private val remoteAuditLogDataSource = mockk<RemoteAuditLogDataSource>()
    private val repository = AuditLogRepositoryImpl(remoteAuditLogDataSource)

    private val fixedInstant = Instant.parse("2025-05-12T00:00:00Z")
    private val dummyLog = AuditLog(
        id = Uuid.random(),
        createdAt = fixedInstant,
        userId = Uuid.random(),
        userName = "Sarah",
        entityId = Uuid.random(),
        entityType = AuditLog.EntityType.TASK,
        entityName = "Task #123",
        actionType = AuditLog.ActionType.CREATE,
        fieldChange = AuditLog.FieldChange(
            fieldName = "status",
            oldValue = "pending",
            newValue = "completed"
        )
    )

    @Test
    fun `should return audit log when createAuditLog succeeds`() = runTest {
        coEvery { remoteAuditLogDataSource.saveAuditLog(dummyLog) } returns dummyLog

        val result = repository.createAuditLog(dummyLog)

        assertThat(result).isEqualTo(dummyLog)
        coVerify { remoteAuditLogDataSource.saveAuditLog(dummyLog) }
    }

    @Test
    fun `should throw AuditLogCreationFailedException when createAuditLog fails`() = runTest {
        coEvery { remoteAuditLogDataSource.saveAuditLog(dummyLog) } throws RuntimeException()

        assertThrows(AuditLogCreationFailedException::class.java) {
            runBlocking { repository.createAuditLog(dummyLog) }
        }
    }

    @Test
    fun `should complete successfully when deleteAuditLog succeeds`() = runTest {
        coEvery { remoteAuditLogDataSource.deleteAuditLog(dummyLog.id) } returns Unit

        repository.deleteAuditLog(dummyLog.id)

        coVerify { remoteAuditLogDataSource.deleteAuditLog(dummyLog.id) }
    }

    @Test
    fun `should throw AuditLogDeletionFailedException when deleteAuditLog fails`() = runTest {
        coEvery { remoteAuditLogDataSource.deleteAuditLog(dummyLog.id) } throws RuntimeException()

        assertThrows(AuditLogDeletionFailedException::class.java) {
            runBlocking { repository.deleteAuditLog(dummyLog.id) }
        }
    }

    @Test
    fun `should return entity logs when getEntityLogs succeeds`() = runTest {
        val expectedLogs = listOf(dummyLog)
        coEvery {
            remoteAuditLogDataSource.getEntityLogs(dummyLog.entityId, dummyLog.entityType)
        } returns expectedLogs

        val result = repository.getEntityLogs(dummyLog.entityId, dummyLog.entityType)

        assertThat(result).containsExactly(*expectedLogs.toTypedArray())
        coVerify { remoteAuditLogDataSource.getEntityLogs(dummyLog.entityId, dummyLog.entityType) }
    }

    @Test
    fun `should throw AuditLogNotFoundException when getEntityLogs fails`() = runTest {
        coEvery {
            remoteAuditLogDataSource.getEntityLogs(dummyLog.entityId, dummyLog.entityType)
        } throws RuntimeException()

        assertThrows(AuditLogNotFoundException::class.java) {
            runBlocking { repository.getEntityLogs(dummyLog.entityId, dummyLog.entityType) }
        }
    }

    @Test
    fun `should return audit log when getEntityLogByLogId succeeds`() = runTest {
        coEvery { remoteAuditLogDataSource.getEntityLogByLogId(dummyLog.id) } returns dummyLog

        val result = repository.getEntityLogByLogId(dummyLog.id)

        assertThat(result).isEqualTo(dummyLog)
        coVerify { remoteAuditLogDataSource.getEntityLogByLogId(dummyLog.id) }
    }

    @Test
    fun `should throw AuditLogNotFoundException when getEntityLogByLogId fails`() = runTest {
        coEvery { remoteAuditLogDataSource.getEntityLogByLogId(dummyLog.id) } throws RuntimeException()

        assertThrows(AuditLogNotFoundException::class.java) {
            runBlocking { repository.getEntityLogByLogId(dummyLog.id) }
        }
    }
}
