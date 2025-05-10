package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import mockdata.createProject
import mockdata.createUser
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.CreateProjectUseCase
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase
    private lateinit var createProjectUseCase: CreateProjectUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        auditLogRepository = mockk(relaxed = true)
        authenticationRepository = mockk(relaxed = true)
        getCurrentUserUseCase = mockk(relaxed = true)
        createProjectUseCase =
            CreateProjectUseCase(
                projectRepository,
                auditLogRepository,
                getCurrentUserUseCase,
            )
    }

    @Test
    fun `should return created project when the input is not blank and user is an admin`() =
        runTest {
            val projectName = "Test Project"
            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
            coEvery { projectRepository.createProject(any()) } returns createProject(name = projectName)

            val createdProject = createProjectUseCase(projectName)

            coVerify { projectRepository.createProject(any()) }
            coVerify { auditLogRepository.createAuditLog(any()) }
            assertThat(createdProject.name).isEqualTo(projectName)
        }

    @Test
    fun `should return created project with default list of 3 states when the input is not blank and user is an admin`() =
        runTest {
            val projectName = "Test Project"
            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
            coEvery { projectRepository.createProject(any()) } returns createProject(name = projectName)

            val createdProject = createProjectUseCase(projectName)

            coVerify { projectRepository.createProject(any()) }
            coVerify { auditLogRepository.createAuditLog(any()) }
            assertThat(createdProject.states).hasSize(3)
        }

    @Test
    fun `should return created project with one item in auditLogsIds when the input is not blank and user is an admin`() =
        runTest {
            val projectName = "Test Project"
            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
            coEvery { projectRepository.createProject(any()) } returns
                createProject(
                    name = projectName,
                    auditLogsIds = listOf("1"),
                )

            val createdProject = createProjectUseCase(projectName)

            coVerify { projectRepository.createProject(any()) }
            coVerify { auditLogRepository.createAuditLog(any()) }
            assertThat(createdProject.auditLogsIds).isNotEmpty()
            assertThat(createdProject.auditLogsIds).hasSize(1)
        }

    @Test
    fun `should throw BlankInputException when projectName is blank`() =
        runTest {
            val projectName = ""
            coEvery { authenticationRepository.getCurrentUser() } returns createUser()

            assertThrows<BlankInputException> {
                createProjectUseCase(projectName)
            }
        }

    @Test
    fun `should throw ProjectCreationFailedException when projectName is larger than 16`() =
        runTest {
            val projectName = "plan mate plan mate plan mate plan mate plan mate"
            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)

            assertThrows<ProjectCreationFailedException> {
                createProjectUseCase(projectName)
            }
        }

    @Test
    fun `should throws ProjectCreationFailedException when audit log return exception`() =
        runTest {
            val projectName = "Test Project"
            coEvery { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
            coEvery { auditLogRepository.createAuditLog(any()) } throws CreationItemFailedException("")

            assertThrows<CreationItemFailedException> {
                createProjectUseCase(projectName)
            }
        }
}
