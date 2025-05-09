package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import mockdata.createAuditLog
import mockdata.createProject
import mockdata.createState
import mockdata.createUser
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.useCase.UpdateProjectUseCase
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateProjectUseCaseTest {
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var projectRepository: ProjectRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase
    private lateinit var taskRepository: TaskRepository

    @BeforeEach
    fun setup() {
        projectRepository = mockk(relaxed = true)
        auditLogRepository = mockk(relaxed = true)
        auditLogRepository = spyk<AuditLogRepository>()
        authenticationRepository = mockk(relaxed = true)
        projectRepository = mockk(relaxed = true)
        getProjectTasksUseCase = mockk(relaxed = true)
        taskRepository = mockk(relaxed = true)
        updateProjectUseCase = UpdateProjectUseCase(
            projectRepository, auditLogRepository, authenticationRepository,
        )
    }

    @Test
    fun `should throws UnauthorizedException when mateUser request updating project`() = runTest {
        val updatedProject = createProject(name = "updated project")
        coEvery { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.USER)

        assertThrows<UnauthorizedException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throw BlankInputException when project name is empty`() = runTest {
        val updatedProject = createProject(name = "")

        assertThrows<BlankInputException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throws ProjectNotFoundException when project does not exist`() = runTest {
        val updatedProject = createProject(name = "updated project")
        coEvery { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.ADMIN)
        coEvery { projectRepository.getProjectById(updatedProject.id) } throws ProjectNotFoundException("No Project Found")

        assertThrows<ProjectNotFoundException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throws ProjectNotChangedException when audit updating project log return exception`() = runTest {
        val updatedProject = createProject(name = "Plan")
        coEvery { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.ADMIN)
        coEvery { projectRepository.getProjectById(updatedProject.id) } returns createProject()
        coEvery { auditLogRepository.createAuditLog(any()) } throws UpdateItemFailedException("")

        assertThrows<UpdateItemFailedException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throw NoLoggedInUserException when no user is logged in`() = runTest {
        val updatedProject = createProject(name = "PlanMate")
        coEvery { authenticationRepository.getCurrentUser() } returns null

        assertThrows<NoLoggedInUserException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throw ProjectNotFoundException when project does not exist`() = runTest {
        val updatedProject = createProject(name = "Updated")
        coEvery { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.ADMIN)
        coEvery { projectRepository.getProjectById(updatedProject.id) } throws ProjectNotFoundException("No Project Found")

        assertThrows<ProjectNotFoundException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should update project successfully when only name is changed`() = runTest {
        val originalProject = createProject(id = "1", "plans mate")
        val updatedProject = createProject(id = "1", "plan mate")
        val currentUser = createUser(role = UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        coEvery { authenticationRepository.getCurrentUser() } returns currentUser
        coEvery { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        coEvery { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
    }

    @Test
    fun `should update project successfully when a state is updated`() = runTest {
        val originalProject = createProject(
            id = "1",
            name = "new",
            states = listOf(createState(title = "ToDO"), createState(title = "InProgress"), createState(title = "Done"))
        )
        val updatedProject = createProject(
            id = "1",
            name = "new",
            states = listOf(createState(title = "ToDO"), createState(title = "InReview"), createState(title = "Done"))
        )
        val currentUser = createUser(role = UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        coEvery { authenticationRepository.getCurrentUser() } returns currentUser
        coEvery { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        coEvery { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
        coVerify { auditLogRepository.createAuditLog(any()) }
    }

    @Test
    fun `should update project successfully when a state is added`() = runTest {
        val originalProject = createProject(id = "1", name = "new", states = listOf())
        val updatedProject = createProject(id = "1", name = "new", states = listOf(createState(title = "ToDO")))
        val currentUser = createUser(role = UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        coEvery { authenticationRepository.getCurrentUser() } returns currentUser
        coEvery { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        coEvery { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
        coVerify { auditLogRepository.createAuditLog(any()) }
    }

    @Test
    fun `should update project successfully when a state is deleted`() = runTest {
        val originalProject = createProject(
            id = "1",
            name = "new",
            states = listOf(createState(title = "ToDO"), createState(title = "InProgress"), createState(title = "Done"))
        )
        val updatedProject = createProject(
            id = "1", name = "new", states = listOf(createState(title = "ToDO"), createState(title = "InReview"))
        )
        val currentUser = createUser(role = UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        coEvery { authenticationRepository.getCurrentUser() } returns currentUser
        coEvery { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        coEvery { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
        coVerify { auditLogRepository.createAuditLog(any()) }

    }
}