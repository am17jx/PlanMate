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

class DeleteProjectUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var taskRepository: TaskRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var deleteProjectUseCase: DeleteProjectUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        taskRepository = mockk()
        auditLogRepository = mockk()
        deleteProjectUseCase = DeleteProjectUseCase(projectRepository, taskRepository, auditLogRepository)
    }

    @Test
    fun `should delete project and its tasks successfully when user is admin`() {
        val user = User("admin", "hashedPassword", UserRole.ADMIN)
        val projectId = "project-001"
        val project = Project(projectId, "Test Project", mutableListOf())

        every { projectRepository.getProjectById(projectId) } returns project
        every { taskRepository.deleteTasksByProjectId(projectId) } returns true
        every { projectRepository.deleteProject(projectId) } returns true
        every { auditLogRepository.logDeleteAction(user, "project-$projectId", "project") } just Runs

        val result = deleteProjectUseCase.execute(user, projectId)

        assertThat(result).isTrue()
        verify { auditLogRepository.logDeleteAction(user, "project-$projectId", "project") }
    }

    @Test
    fun `should throw exception if user is not admin`() {
        val user = User("mate", "hashedPassword", UserRole.MATE)
        val projectId = "project-002"
        val project = Project(projectId, "Test Project", mutableListOf())

        every { projectRepository.getProjectById(projectId) } returns project

        assertThrows<IllegalArgumentException> {
            deleteProjectUseCase.execute(user, projectId)
        }
    }

    @Test
    fun `should throw exception if project not found`() {
        val user = User("admin", "hashedPassword", UserRole.ADMIN)
        val projectId = "project-003"

        every { projectRepository.getProjectById(projectId) } returns null

        assertThrows<BlankInputException> {
            deleteProjectUseCase.execute(user, projectId)
        }
    }
}

