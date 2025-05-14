package logic.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLog.FieldChange
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.GetCurrentUserUseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.google.common.truth.Truth.assertThat
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateAuditLogUseCaseTest {

    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase

    private val testUser = User(
        id = Uuid.random(),
        username = "tester",
        role = UserRole.ADMIN,
        authMethod = User.AuthenticationMethod.Password("test123")
    )

    private val entityId = Uuid.random()
    private val entityName = "TestEntity"

    @BeforeEach
    fun setUp() {
        auditLogRepository = mockk(relaxed = true)
        getCurrentUserUseCase = mockk()
        createAuditLogUseCase = CreateAuditLogUseCase(auditLogRepository, getCurrentUserUseCase)

        coEvery { getCurrentUserUseCase() } returns testUser
    }

    @Test
    fun `should call createAuditLog with correct CREATE action when logCreation is called`() = runTest {
        coEvery { auditLogRepository.createAuditLog(any()) } returns mockk()

        createAuditLogUseCase.logCreation(
            entityType = AuditLog.EntityType.PROJECT,
            entityId = entityId,
            entityName = entityName,
        )

        coVerify {
            auditLogRepository.createAuditLog(
                withArg {
                    assertThat(it.actionType).isEqualTo(AuditLog.ActionType.CREATE)
                    assertThat(it.userId).isEqualTo(testUser.id)
                    assertThat(it.entityType).isEqualTo(AuditLog.EntityType.PROJECT)
                }
            )
        }
    }

    @Test
    fun `should call createAuditLog with correct UPDATE action and field change when logUpdate is called`() = runTest {
        val fieldChange = FieldChange("field", "old", "new")
        coEvery { auditLogRepository.createAuditLog(any()) } returns mockk()

        createAuditLogUseCase.logUpdate(
            entityType = AuditLog.EntityType.TASK,
            entityId = entityId,
            entityName = entityName,
            fieldChange = fieldChange,
        )

        coVerify {
            auditLogRepository.createAuditLog(
                withArg {
                    assertThat(it.actionType).isEqualTo(AuditLog.ActionType.UPDATE)
                    assertThat(it.fieldChange).isEqualTo(fieldChange)
                }
            )
        }
    }

    @Test
    fun `should call createAuditLog with correct DELETE action when logDeletion is called`() = runTest {
        coEvery { auditLogRepository.createAuditLog(any()) } returns mockk()

        createAuditLogUseCase.logDeletion(
            entityType = AuditLog.EntityType.TASK,
            entityId = entityId,
            entityName = entityName,
        )

        coVerify {
            auditLogRepository.createAuditLog(
                withArg {
                    assertThat(it.actionType).isEqualTo(AuditLog.ActionType.DELETE)
                    assertThat(it.fieldChange).isNull()
                }
            )
        }
    }
}
