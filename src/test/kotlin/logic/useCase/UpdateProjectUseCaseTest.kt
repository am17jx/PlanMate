package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import mockdata.createAuditLog
import mockdata.createProject
import mockdata.createState
import mockdata.createUser
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
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
            projectRepository,
            auditLogRepository,
            authenticationRepository,
            getProjectTasksUseCase,
            taskRepository
        )
    }

    @Test
    fun `should throws UnauthorizedException when mateUser request updating project`() {
        val updatedProject = createProject(name = "updated project")
        every { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.USER)

        assertThrows<UnauthorizedException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throw BlankInputException when project name is empty`() {
        val updatedProject = createProject(name = "")

        assertThrows<BlankInputException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throws ProjectNotFoundException when project does not exist`() {
        val updatedProject = createProject(name = "updated project")
        every { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.ADMIN)
        every { projectRepository.getProjectById(updatedProject.id) } throws ProjectNotFoundException("No Project Found")

        assertThrows<ProjectNotFoundException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throws ProjectNotChangedException when audit updating project log return exception`() {
        val updatedProject = createProject(name = "Plan")
        every { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.ADMIN)
        every { projectRepository.getProjectById(updatedProject.id) } returns createProject()
        every { auditLogRepository.createAuditLog(any()) } throws Exception()
        //every { auditLogRepository.deleteAuditLog(any()) } throws Exception()


        assertThrows<ProjectNotChangedException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throw NoLoggedInUserException when no user is logged in`() {
        val updatedProject = createProject(name = "PlanMate")
        every { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.ADMIN)
        every { authenticationRepository.getCurrentUser() } returns null

        assertThrows<NoLoggedInUserException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throw ProjectNotFoundException when project does not exist`() {
        val updatedProject = createProject(name = "Updated")
        every { authenticationRepository.getCurrentUser() } returns createUser(role = UserRole.ADMIN)
        every { projectRepository.getProjectById(updatedProject.id) } throws ProjectNotFoundException("No Project Found")

        assertThrows<ProjectNotFoundException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should update project successfully when only name is changed`() {
        val originalProject = createProject(id = "1", "plans mate")
        val updatedProject = createProject(id = "1", "plan mate")
        val currentUser = createUser(role = UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        every { authenticationRepository.getCurrentUser() } returns currentUser
        every { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        every { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
    }

    @Test
    fun `should update project successfully when a state is updated`() {
        val originalProject = createProject(
            id = "1", name = "new",
            states = listOf(createState(title = "ToDO"), createState(title = "InProgress"), createState(title = "Done"))
        )
        val updatedProject = createProject(
            id = "1", name = "new",
            states = listOf(createState(title = "ToDO"), createState(title = "InReview"), createState(title = "Done"))
        )
        val currentUser = createUser(role = UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        every { authenticationRepository.getCurrentUser() } returns currentUser
        every { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        every { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
        verify { auditLogRepository.createAuditLog(any()) }
    }

    @Test
    fun `should update project successfully when a state is added`() {
        val originalProject = createProject(id = "1", name = "new", states = listOf())
        val updatedProject = createProject(id = "1", name = "new", states = listOf(createState(title = "ToDO")))
        val currentUser = createUser(role = UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        every { authenticationRepository.getCurrentUser() } returns currentUser
        every { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        every { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
        verify { auditLogRepository.createAuditLog(any()) }
    }

    @Test
    fun `should update project successfully when a state is deleted`() {
        val originalProject = createProject(
            id = "1", name = "new",
            states = listOf(createState(title = "ToDO"), createState(title = "InProgress"), createState(title = "Done"))
        )
        val updatedProject =
            createProject(
                id = "1",
                name = "new",
                states = listOf(createState(title = "ToDO"), createState(title = "InReview"))
            )
        val currentUser = createUser(role = UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        every { authenticationRepository.getCurrentUser() } returns currentUser
        every { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        every { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
        verify { auditLogRepository.createAuditLog(any()) }

    }
}