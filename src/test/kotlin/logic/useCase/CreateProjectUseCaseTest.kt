package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mockdata.createProject
import mockdata.createUser
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.CreateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.NoLoggedInUserException
import org.example.logic.utils.ProjectCreationFailedException
import org.example.logic.utils.UnauthorizedException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreateProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var createProjectUseCase: CreateProjectUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        auditLogRepository = mockk(relaxed = true)
        authenticationRepository = mockk(relaxed = true)
        createProjectUseCase =
            CreateProjectUseCase(
                projectRepository,
                auditLogRepository,
                authenticationRepository,
            )
    }

    @Test
    fun `should return created project when the input is not blank and user is an admin`() {
        val projectName = "Test Project"
        every { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
        every { projectRepository.createProject(any()) } returns createProject(name = projectName)

        val createdProject = createProjectUseCase(projectName)

        verify { projectRepository.createProject(any()) }
        verify { authenticationRepository.getCurrentUser() }
        verify { auditLogRepository.createAuditLog(any()) }
        assertThat(createdProject.name).isEqualTo(projectName)
    }

    @Test
    fun `should return created project with empty list of states when the input is not blank and user is an admin`() {
        val projectName = "Test Project"
        every { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
        every { projectRepository.createProject(any()) } returns createProject(name = projectName)

        val createdProject = createProjectUseCase(projectName)

        verify { projectRepository.createProject(any()) }
        verify { authenticationRepository.getCurrentUser() }
        verify { auditLogRepository.createAuditLog(any()) }
        assertThat(createdProject.states).isEmpty()
    }

    @Test
    fun `should return created project with one item in auditLogsIds when the input is not blank and user is an admin`() {
        val projectName = "Test Project"
        every { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.ADMIN)
        every { projectRepository.createProject(any()) } returns
            createProject(
                name = projectName,
                auditLogsIds = listOf("1"),
            )

        val createdProject = createProjectUseCase(projectName)

        verify { projectRepository.createProject(any()) }
        verify { authenticationRepository.getCurrentUser() }
        verify { auditLogRepository.createAuditLog(any()) }
        assertThat(createdProject.auditLogsIds).isNotEmpty()
        assertThat(createdProject.auditLogsIds).hasSize(1)
    }

    @Test
    fun `should throw BlankInputException when projectName is blank`() {
        val projectName = ""
        every { authenticationRepository.getCurrentUser() } returns createUser()

        assertThrows<BlankInputException> {
            createProjectUseCase(projectName)
        }
    }

    @Test
    fun `should throw ProjectCreationFailedException when given project name is too long`() {
        val projectName = "Test Project from the main to my life"
        every { authenticationRepository.getCurrentUser() } returns createUser()

        assertThrows<ProjectCreationFailedException> {
            createProjectUseCase(projectName)
        }
    }

    @Test
    fun `should throw UnauthorizedException when user is not an admin`() {
        val projectName = "Test Project"
        every { authenticationRepository.getCurrentUser() } returns User("", "", "", UserRole.USER)

        assertThrows<UnauthorizedException> {
            createProjectUseCase(projectName)
        }
    }

    @Test
    fun `should throw NoLoggedInUserException when current user is null`() {
        val projectName = "Test Project"
        every { authenticationRepository.getCurrentUser() } returns null

        assertThrows<NoLoggedInUserException> {
            createProjectUseCase(projectName)
        }
    }
}
