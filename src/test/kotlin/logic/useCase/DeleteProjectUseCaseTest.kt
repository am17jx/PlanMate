package logic.useCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.useCase.deleteProject.DeleteProjectUseCase
import org.example.logic.useCase.deleteTask.DeleteTaskUseCase
import org.example.logic.utils.BlankInputException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var userUseCase: GetCurrentUserUseCase
    private lateinit var deleteProjectUseCase: DeleteProjectUseCase
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase

    private val dummyProject = Project(id = "project1", name = "Test Project", states = listOf(), auditLogsIds = listOf())
    private val dummyUser = User(id = "user1", username = "testUser", password = "testPassword", role = UserRole.ADMIN,)
    private val auditLog= AuditLog(id = "1", userId = "user1", action = "test", timestamp = 123456, entityType = AuditLogEntityType.PROJECT, entityId = "project1", actionType = AuditLogActionType.DELETE)

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        auditLogRepository = mockk()
        userUseCase = mockk()
        getProjectTasksUseCase = mockk()
        taskRepository = mockk()
        deleteProjectUseCase = DeleteProjectUseCase(
            projectRepository,
            auditLogRepository,
            userUseCase,
            getProjectTasksUseCase,
            taskRepository
        )
    }

    @Test
    fun `should execute delete project and save audit log when project id is valid`() {
        every { userUseCase() } returns dummyUser
        every { projectRepository.getProjectById("project1") } returns dummyProject
        every { projectRepository.deleteProject("project1") } returns Unit
        every { auditLogRepository.createAuditLog (any()) } returns auditLog

        deleteProjectUseCase("project1")

        verify { userUseCase() }
        verify { projectRepository.deleteProject("project1") }
        verify { auditLogRepository.createAuditLog(any()) }
    }

    @Test
    fun `should throw BlankInputException when project id is blank`() {
        assertThrows<BlankInputException> {
            deleteProjectUseCase("")
        }
    }
}

class DeleteTaskCommandUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var userUseCase: GetCurrentUserUseCase
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private val dummyTask = Task(id = "task1", name = "Test Task", stateId = "state1", addedBy = "user1", auditLogsIds = listOf(), projectId = "project1")
    private val dummyUser = User(id = "user1", username = "testUser", password = "testPassword", role = UserRole.ADMIN)
    private val auditLog= AuditLog(id = "1", userId = "user1", action = "test", timestamp = 123456, entityType = AuditLogEntityType.PROJECT, entityId = "project1", actionType = AuditLogActionType.DELETE)

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        auditLogRepository = mockk()
        userUseCase = mockk()
        getTaskByIdUseCase = mockk()
        deleteTaskUseCase = DeleteTaskUseCase(
            taskRepository,
            auditLogRepository,
            userUseCase,
            getTaskByIdUseCase
        )
    }

    @Test
    fun `should execute delete task and save audit log when task id is valid`() {
        every { userUseCase() } returns dummyUser
        every { getTaskByIdUseCase("task1") } returns dummyTask
        every { taskRepository.deleteTask("task1") } returns Unit
        every { auditLogRepository.createAuditLog(any()) } returns auditLog

        deleteTaskUseCase("task1")

        verify { userUseCase() }
        verify { getTaskByIdUseCase("task1") }
        verify { taskRepository.deleteTask("task1") }
        verify { auditLogRepository.createAuditLog(any()) }
    }

    @Test
    fun `should throw BlankInputException when task id is blank`() {
        assertThrows<BlankInputException> {
            deleteTaskUseCase("")
        }
    }
}