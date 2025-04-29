package logic.usecases

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.usecases.UpdateProjectUseCase
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UpdateProjectUseCaseTest {
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var projectRepository: ProjectRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var authenticationRepository: AuthenticationRepository

    @BeforeEach
    fun setup() {
        projectRepository = mockk(relaxed = true)
        auditLogRepository = mockk(relaxed = true)
        authenticationRepository = mockk(relaxed = true)
        updateProjectUseCase = UpdateProjectUseCase(projectRepository, auditLogRepository, authenticationRepository)
    }

    @Test
    fun `should throws UnauthorizedException when mateUser request updating project`() {
        val updatedProject = createProject()
        every { authenticationRepository.getCurrentUser() } returns createUser(UserRole.USER)
        //every { projectRepository.getProjectById(updatedProject.id) } returns createProject()

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
        val updatedProject = createProject()
        every { authenticationRepository.getCurrentUser() } returns createUser(UserRole.ADMIN)
        every { projectRepository.getProjectById(updatedProject.id) } throws ProjectNotFoundException("No Project Found")

        assertThrows<ProjectNotFoundException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should throws ProjectNotChangedException when audit updating project log return exception`() {
        val updatedProject = createProject(name = "Plan")
        every { authenticationRepository.getCurrentUser() } returns createUser(UserRole.ADMIN)
        every { projectRepository.getProjectById(updatedProject.id) } returns createProject()
        every { auditLogRepository.createAuditLog(createAuditLog()) } throws AuditInputException("couldn't save log")

        assertThrows<ProjectNotChangedException> {
            updateProjectUseCase(updatedProject)
        }
    }

    @Test
    fun `should update project successfully when only name is changed`() {
        val originalProject = createProject(id = "1", "plans mate")
        val updatedProject = createProject(id = "1", "plan mate")
        val currentUser = createUser(UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        every { authenticationRepository.getCurrentUser() } returns currentUser
        every { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        every { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
    }

    @Test
    fun `should update project successfully when a state is updated`() {
        val originalProject = createProject(id = "1", states = listOf(createState(title = "ToDO"),createState(title = "InProgress"),createState(title = "Done")))
        val updatedProject =  createProject(id = "1", states = listOf(createState(title = "ToDO"),createState(title = "InReview"),createState(title = "Done")))
        val currentUser = createUser(UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        every { authenticationRepository.getCurrentUser() } returns currentUser
        every { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        every { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
    }
    @Test
    fun `should update project successfully when a state is added`() {
        val originalProject = createProject(id = "1", states = listOf())
        val updatedProject =  createProject(id = "1", states = listOf(createState(title = "ToDO")))
        val currentUser = createUser(UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        every { authenticationRepository.getCurrentUser() } returns currentUser
        every { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        every { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)
    }
    @Test
    fun `should update project successfully when a state is deleted`() {
        val originalProject = createProject(id = "1", states = listOf(createState(title = "ToDO"),createState(title = "InProgress"),createState(title = "Done")))
        val updatedProject =  createProject(id = "1", states = listOf(createState(title = "ToDO"),createState(title = "InReview")))
        val currentUser = createUser(UserRole.ADMIN)
        val auditLog = createAuditLog("2", userId = currentUser.id)
        every { authenticationRepository.getCurrentUser() } returns currentUser
        every { projectRepository.getProjectById(updatedProject.id) } returns originalProject
        every { auditLogRepository.createAuditLog(createAuditLog()) } returns auditLog

        val result = updateProjectUseCase(updatedProject)

        assertThat(result.name).isEqualTo(updatedProject.name)

    }
    fun createProject(
        id: String = "1",
        name: String = "plan mate",
        states: List<State> = emptyList(),
        auditLogsIds: List<String> = emptyList()
    ) = Project(
        id = id,
        name = name,
        states = states,
        auditLogsIds = auditLogsIds
    )

    fun createUser(
        role: UserRole
    ) = User(
        id = "1",
        username = "",
        password = "",
        role = role
    )


    fun createAuditLog(
        id: String = "",
        userId: String = "",
        action: String = "",
        timestamp: Long = 0,
        entityType: AuditLogEntityType = AuditLogEntityType.PROJECT,
        entityId: String = "",
        actionType: AuditLogActionType = AuditLogActionType.UPDATE
    ) = AuditLog(
        id = id,
        userId = userId,
        action = action,
        timestamp = timestamp,
        entityType = entityType,
        entityId = entityId,
        actionType = actionType
    )

    fun createState(
        id: String = "",
        title: String = ""
    ) = State(
        id = id,
        title = title
    )

}