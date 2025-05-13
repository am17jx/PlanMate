package org.example.logic.useCase.updateProject

import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLog.FieldChange.Companion.detectChanges
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.Validation
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotChangedException
import org.example.logic.utils.ProjectNotFoundException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateProjectUseCaseTest {

    private lateinit var useCase: UpdateProjectUseCase
    private lateinit var repo: ProjectRepository
    private lateinit var auditLogUC: CreateAuditLogUseCase
    private lateinit var validation: Validation

    @BeforeTest
    fun setup() {
        repo = mockk(relaxed = true)
        auditLogUC = mockk(relaxed = true)
        validation = mockk(relaxed = true)

        useCase = UpdateProjectUseCase(
            projectRepository = repo,
            createAuditLogUseCase = auditLogUC,
            validation = validation
        )
    }

    @Test
    fun `when name blank then throws BlankInputException`() = runTest {
        // arrange
        val id = Uuid.random()
        val updated = Project(id = id, name = "")
        every { validation.validateInputNotBlankOrThrow("") } throws BlankInputException()

        // act & assert
        assertFailsWith<BlankInputException> {
            useCase(updated)
        }
    }

    @Test
    fun `when project not found then throws ProjectNotFoundException`() = runTest {
        // arrange
        val id = Uuid.random()
        val updated = Project(id = id, name = "New name")
        every { validation.validateInputNotBlankOrThrow("New name") } just Runs
        coEvery { repo.getProjectById(id) } returns null

        // act & assert
        assertFailsWith<ProjectNotFoundException> {
            useCase(updated)
        }
    }

    @Test
    fun `when no change in name then throws ProjectNotChangedException`() = runTest {
        // arrange
        val id = Uuid.random()
        val original = Project(id = id, name = "Same")
        val updated = Project(id = id, name = "Same")
        every { validation.validateInputNotBlankOrThrow("Same") } just Runs
        coEvery { repo.getProjectById(id) } returns original

        // act & assert
        assertFailsWith<ProjectNotChangedException> {
            useCase(updated)
        }
    }

    @Test
    fun `when name changed then calls update and logs each field change`() = runTest {
        // arrange
        val id = Uuid.random()
        val original = Project(id = id, name = "Old")
        val updated = Project(id = id, name = "New")

        // لا حاجة لموك detectChanges لأنه تنفيذ حقيقي
        val changes = listOf(
            AuditLog.FieldChange(fieldName = "name", oldValue = "Old", newValue = "New")
        )
        // ننفذ الفحص الحقيقي على الكائنين
        assertEquals(changes, updated.detectChanges(original))

        // ضبط الموكات
        every { validation.validateInputNotBlankOrThrow("New") } just Runs
        coEvery { repo.getProjectById(id) } returns original
        coEvery { repo.updateProject(updated) } returns updated
        coEvery {
            auditLogUC.logUpdate(
                entityType = AuditLog.EntityType.PROJECT,
                entityId = id,
                entityName = "New",
                fieldChange = changes[0]
            )
        } returns AuditLog(
            userId = Uuid.random(),
            userName = "tester",
            entityId = id,
            entityType = AuditLog.EntityType.PROJECT,
            entityName = "New",
            actionType = AuditLog.ActionType.UPDATE,
            fieldChange = changes[0]
        )

        // act
        val result = useCase(updated)

        // assert
        assertEquals("New", result.name)
        coVerify(exactly = 1) { repo.updateProject(updated) }
        coVerify(exactly = 1) {
            auditLogUC.logUpdate(
                entityType = AuditLog.EntityType.PROJECT,
                entityId = id,
                entityName = "New",
                fieldChange = changes[0]
            )
        }
    }
}
