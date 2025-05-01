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


class DeleteTasksByProjectUseCaseTest {


    private lateinit var taskRepository: TaskRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var deleteTasksByProjectUseCase: DeleteTasksByProjectUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        auditLogRepository = mockk()
        deleteTasksByProjectUseCase = DeleteTasksByProjectUseCase(taskRepository, auditLogRepository)
    }

    @Test
    fun `should delete tasks for project successfully`() {
        val user = User("admin", "hashedPassword", UserRole.ADMIN)
        val projectId = "project-001"
        val tasks = listOf(Task("task-001", "Task 1", "TODO", projectId), Task("task-002", "Task 2", "IN_PROGRESS", projectId))

        every { taskRepository.getTasksByProjectId(projectId) } returns tasks
        every { taskRepository.deleteTask(any()) } returns true
        every { auditLogRepository.logDeleteAction(user, any(), projectId) } just Runs

        val result = deleteTasksByProjectUseCase.execute(user, projectId)

        assertThat(result).isTrue()
        verify(exactly = 2) { auditLogRepository.logDeleteAction(user, any(), projectId) }
    }

    @Test
    fun `should throw exception when no tasks are found for given project`() {
        val user = User("admin", "hashedPassword", UserRole.ADMIN)
        val projectId = "project-002"

        every { taskRepository.getTasksByProjectId(projectId) } returns emptyList()

        assertThrows<BlankInputException> {
            deleteTasksByProjectUseCase.execute(user, projectId)
        }
    }

    @Test
    fun `should throw exception when user has no permission to delete tasks`() {
        val user = User("mate", "hashedPassword", UserRole.MATE)
        val projectId = "project-003"
        val tasks = listOf(Task("task-003", "Task 1", "TODO", projectId))

        every { taskRepository.getTasksByProjectId(projectId) } returns tasks

        assertThrows<IllegalArgumentException> {
            deleteTasksByProjectUseCase.execute(user, projectId)
        }
    }
}
