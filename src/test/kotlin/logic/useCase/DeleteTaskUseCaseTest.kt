package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.CreateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        auditLogRepository = mockk()
        authenticationRepository = mockk()
        deleteTaskUseCase = DeleteTaskUseCase(taskRepository, auditLogRepository, authenticationRepository)
    }

    @Test
    fun `should delete task successfully when user is admin`() {
        val user = User("admin", "hashedPassword", UserRole.ADMIN)
        val taskId = "task-001"
        val projectId = "project-001"
        val task = Task(taskId, "Test Task", "TODO", projectId)

        every { taskRepository.getTaskById(taskId) } returns task
        every { taskRepository.deleteTask(taskId) } returns true
        every { auditLogRepository.logDeleteAction(user, taskId, projectId) } just Runs

        val result = deleteTaskUseCase.execute(user, taskId, projectId)


        assertThat(result).isTrue()
        verify { auditLogRepository.logDeleteAction(user, taskId, projectId) }
    }

    @Test
    fun `should throw BlankInputException when taskId is blank`() {
        val user = User("mate", "hashedPassword", UserRole.MATE)
        val taskId = ""
        val projectId = "project-001"

        assertThrows<BlankInputException> {
            deleteTaskUseCase.execute(user, taskId, projectId)
        }
    }

    fun `should throw ProjectNotFoundException when project is not found`() {
        val user = User("mate", "hashedPassword", UserRole.MATE)
        val taskId = "task-002"
        val projectId = "project-001"

        every { projectRepository.getProjectById(projectId) } returns null

        assertThrows<ProjectNotFoundException> {
            deleteTaskUseCase.execute(user, taskId, projectId)
        }
    }



    @Test
    fun `should throw exception if user is not admin and does not have permission to delete task`() {
        val user = User("mate", "hashedPassword", UserRole.MATE)
        val taskId = "task-003"
        val projectId = "project-001"
        val task = Task(taskId, "Test Task", "TODO", "another-project-id")


        every { taskRepository.getTaskById(taskId) } returns task


        assertThrows<IllegalArgumentException> {
            deleteTaskUseCase.execute(user, taskId, projectId)
        }
    }
}


