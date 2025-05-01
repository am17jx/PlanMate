package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import mockdata.createAuditLog
import org.example.logic.models.AuditLogEntityType
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
        entityId: String, entityType: AuditLogEntityType
    ) {
        every { auditLogRepository.getEntityLogs(any(), any()) } returns listOf(createAuditLog(entityId = entityId, entityType = entityType))

        val result = getEntityAuditLogsUseCase(entityId, entityType)

        assertThat(result).isNotEmpty()
    }

    @Test
    fun `should throw TaskNotFoundException when entity type is Task and there is no logs for it`(){
        val taskId = Uuid.random().toHexString()
        every { auditLogRepository.getEntityLogs(any(), any()) } returns emptyList()

        assertThrows<TaskNotFoundException> {
            getEntityAuditLogsUseCase(taskId, AuditLogEntityType.TASK)
        }
    }

    @Test
    fun `should throw ProjectNotFoundException when entity type is Project and there is no logs for it`(){
        val projectId = Uuid.random().toHexString()
        every { auditLogRepository.getEntityLogs(any(), any()) } returns emptyList()

        assertThrows<ProjectNotFoundException> {
            getEntityAuditLogsUseCase(projectId, AuditLogEntityType.PROJECT)
        }
    }

    companion object {

        @JvmStatic
        fun provideExistingEntitiesScenarios(): Stream<Arguments> = Stream.of(
            Arguments.argumentSet(
                "existing task", Uuid.random().toHexString(), AuditLogEntityType.TASK
            ),
            Arguments.argumentSet(
                "existing project", Uuid.random().toHexString(), AuditLogEntityType.PROJECT
            ),
        )
    }

}