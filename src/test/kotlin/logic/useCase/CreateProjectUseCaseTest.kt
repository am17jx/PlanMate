package logic.useCase

import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.CreateProjectUseCase
import org.example.logic.useCase.Validation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

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
//            val projectName = "Test Project"
//            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
//            coEvery { projectRepository.createProject(any()) } returns createProject(name = projectName)
//
//            val createdProject = createProjectUseCase(projectName)
//
//            coVerify { projectRepository.createProject(any()) }
//            coVerify { auditLogRepository.createAuditLog(any()) }
//            assertThat(createdProject.name).isEqualTo(projectName)
            assertTrue(false)
        }

    @Test
    fun `should return created project with default list of 3 states when the input is not blank and user is an admin`() =
        runTest {
//            val projectName = "Test Project"
//            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
//            coEvery { projectRepository.createProject(any()) } returns createProject(name = projectName)
//
//            val createdProject = createProjectUseCase(projectName)
//
//            coVerify { projectRepository.createProject(any()) }
//            coVerify { auditLogRepository.createAuditLog(any()) }
//            assertThat(createdProject.projectStateIds).hasSize(3)
            assertTrue(false)
        }

    @Test
    fun `should return created project with one item in auditLogsIds when the input is not blank and user is an admin`() =
        runTest {
//            val projectName = "Test Project"
//            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
//            coEvery { projectRepository.createProject(any()) } returns
//                createProject(
//                    name = projectName,
//                    auditLogsIds = listOf("1"),
//                )
//
//            val createdProject = createProjectUseCase(projectName)
//
//            coVerify { projectRepository.createProject(any()) }
//            coVerify { auditLogRepository.createAuditLog(any()) }
//            assertThat(createdProject.auditLogsIds).isNotEmpty()
//            assertThat(createdProject.auditLogsIds).hasSize(1)
            assertTrue(false)
        }

    @Test
    fun `should throw BlankInputException when projectName is blank`() =
        runTest {
            // TODO
//            val projectName = ""
//            coEvery { authenticationRepository.getCurrentUser() } returns createUser()
//
//            assertThrows<BlankInputException> {
//                createProjectUseCase(projectName)
//            }
            assertTrue(false)
        }

    @Test
    fun `should throw ProjectCreationFailedException when projectName is larger than 16`() =
        runTest {
//            val projectName = "plan mate plan mate plan mate plan mate plan mate"
//            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
//
//            assertThrows<ProjectCreationFailedException> {
//                createProjectUseCase(projectName)
//            }
            assertTrue(false)
        }

    @Test
    fun `should throws ProjectCreationFailedException when audit log return exception`() =
        runTest {
//            val projectName = "Test Project"
//            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
//            coEvery { auditLogRepository.createAuditLog(any()) } throws ProjectCreationFailedException()
//
//            assertThrows<ProjectCreationFailedException> {
//                createProjectUseCase(projectName)
//            }
            assertTrue(false)
        }
}
