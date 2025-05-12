package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createAuditLog
import org.example.logic.models.AuditLog
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.useCase.GetEntityAuditLogsUseCase
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetEntityAuditLogsUseCaseTest {
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var getEntityAuditLogsUseCase: GetEntityAuditLogsUseCase

    @BeforeEach
    fun setUp() {
        auditLogRepository = mockk(relaxed = true)
        getEntityAuditLogsUseCase = GetEntityAuditLogsUseCase(auditLogRepository)
    }

    @ParameterizedTest
    @MethodSource("provideExistingEntitiesScenarios")
    fun `should return list of audit logs when entity exists`(
        entityId: String,
        entityType: String
    ) = runTest {
        val entityTypeEnum = AuditLog.EntityType.valueOf(entityType)
        val entityUuid = Uuid.parse(entityId)
        coEvery { auditLogRepository.getEntityLogs(any(), any()) } returns listOf(
            createAuditLog(
                entityId = entityUuid, entityType = entityTypeEnum
            )
        )

        val result = getEntityAuditLogsUseCase(entityUuid, entityTypeEnum)

        assertThat(result).isNotEmpty()
    }

    @Test
    fun `should throw TaskNotFoundException when entity type is Task and there is no logs for it`() = runTest {
        val taskId = Uuid.random()
        coEvery { auditLogRepository.getEntityLogs(any(), any()) } returns emptyList()

        assertThrows<TaskNotFoundException> {
            getEntityAuditLogsUseCase(taskId, AuditLog.EntityType.TASK)
        }
    }

    @Test
    fun `should throw ProjectNotFoundException when entity type is Project and there is no logs for it`() = runTest {
        val projectId = Uuid.random()
        coEvery { auditLogRepository.getEntityLogs(any(), any()) } returns emptyList()

        assertThrows<ProjectNotFoundException> {
            getEntityAuditLogsUseCase(projectId, AuditLog.EntityType.PROJECT)
        }
    }

    companion object {

        @JvmStatic
        fun provideExistingEntitiesScenarios(): Stream<Arguments> = Stream.of(
            Arguments.of(
                Uuid.random().toHexString(), AuditLog.EntityType.TASK.name
            ),
            Arguments.of(
                Uuid.random().toHexString(), AuditLog.EntityType.PROJECT.name
            ),
        )
    }

}