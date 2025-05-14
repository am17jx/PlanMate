package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.CreateProjectUseCase
import org.example.logic.useCase.Validation
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectCreationFailedException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class CreateProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var projectStateRepository: ProjectStateRepository
    private lateinit var validation: Validation
    private lateinit var createProjectUseCase: CreateProjectUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        createAuditLogUseCase = mockk(relaxed = true)
        projectStateRepository = mockk(relaxed = true)
        validation = mockk(relaxed = true)
        createProjectUseCase =
            CreateProjectUseCase(
                projectRepository,
                createAuditLogUseCase,
                projectStateRepository,
                validation,
            )
    }

    @Test
    fun `should return created project when the input is not blank and user is an admin`() =
        runTest {
            val projectName = "Test Project"
            coEvery { projectRepository.createProject(any()) } returns createProject(name = projectName)

            val createdProject = createProjectUseCase(projectName)

            verify { validation.validateProjectNameOrThrow(projectName) }
            coVerify { projectRepository.createProject(any()) }
            coVerify { createAuditLogUseCase.logCreation(any(), any(), any()) }
            assertThat(createdProject.name).isEqualTo(projectName)
        }

    @Test
    fun `should throw BlankInputException when projectName is blank`() =
        runTest {
            val projectName = ""
            every { validation.validateProjectNameOrThrow(projectName) } throws BlankInputException()

            assertThrows<BlankInputException> {
                createProjectUseCase(projectName)
            }
        }

    @Test
    fun `should throw ProjectCreationFailedException when projectName is larger than 16`() =
        runTest {
            val projectName = "plan mate plan mate plan mate plan mate plan mate"
            every { validation.validateProjectNameOrThrow(projectName) } throws ProjectCreationFailedException()

            assertThrows<ProjectCreationFailedException> {
                createProjectUseCase(projectName)
            }
        }
}
