package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.example.logic.models.AuditLog
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.Validation
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var validation: Validation
    private lateinit var updateProjectUseCase: UpdateProjectUseCase

    private val projectId = Uuid.random()
    private val originalProject = Project(
        id = projectId,
        name = "Original Project",
    )
    private val updatedProject = Project(
        id = projectId,
        name = "Updated Project",
    )

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        createAuditLogUseCase = mockk(relaxed = true)
        validation = mockk(relaxed = true)
        updateProjectUseCase = UpdateProjectUseCase(
            projectRepository,
            createAuditLogUseCase,
            validation
        )
    }

    @Test
    fun `should update project and create audit logs when project has changes`() = runTest {

        every { validation.validateInputNotBlankOrThrow(updatedProject.name) } just Runs
        coEvery { projectRepository.getProjectById(projectId) } returns originalProject
        coEvery { projectRepository.updateProject(updatedProject) } returns updatedProject

        val result = updateProjectUseCase(updatedProject)

        assertThat(result).isEqualTo(updatedProject)
        coVerify { validation.validateInputNotBlankOrThrow(updatedProject.name) }
        coVerify { projectRepository.getProjectById(projectId) }
        coVerify { projectRepository.updateProject(updatedProject) }
        coVerify {
            createAuditLogUseCase.logUpdate(
                entityId = projectId,
                entityName = updatedProject.name,
                entityType = AuditLog.EntityType.PROJECT,
                fieldChange = match { it.fieldName == "name" && it.oldValue == "Original Project" && it.newValue == "Updated Project" }
            )
        }
    }


    @Test
    fun `should throw exception when project is not found`() = runTest {

        coEvery { projectRepository.getProjectById(projectId) } returns null

        assertThrows<ProjectNotFoundException> {
            updateProjectUseCase(updatedProject)
        }

    }

    @Test
    fun `should throw exception when project name is blank`() = runTest {

        val invalidProject = updatedProject.copy(name = "")
        every { validation.validateInputNotBlankOrThrow("") } throws BlankInputException()

        assertThrows<BlankInputException> {
            updateProjectUseCase(invalidProject)
        }

    }

    @Test
    fun `should throw exception when project name has not changed`() = runTest {

        val sameProject = originalProject.copy(name = "Original Project")
        coEvery { projectRepository.getProjectById(projectId) } returns originalProject

        assertThrows<ProjectNotChangedException> {
            updateProjectUseCase(sameProject)
        }

    }
}