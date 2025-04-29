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

class CreateProjectUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var auditLogRepository: AuditLogRepository
    private lateinit var authenticationRepository: AuthenticationRepository
    private lateinit var createProjectUseCase: CreateProjectUseCase

    @BeforeEach
    fun setUp() {
        projectRepository = mockk()
        auditLogRepository = mockk()
        authenticationRepository = mockk()
        createProjectUseCase =
            CreateProjectUseCase(
                projectRepository,
                auditLogRepository,
                authenticationRepository,
            )
    }

    @Test
    fun `should throw BlankInputException when projectName is blank`() {
        val projectName = ""

        assertThrows<BlankInputException> {
            createProjectUseCase(projectName)
        }
    }

    @Test
    fun `should return project with the same given name when project created successfully`() {
        val projectName = "Test Project"

        val result = createProjectUseCase(projectName)

        assertThat(result.name).isEqualTo(projectName)
    }

    @Test
    fun `should return project with empty list of states when project created successfully`() {
        val projectName = "Test Project"

        val result = createProjectUseCase(projectName)

        assertThat(result.auditLogsIds).isEmpty()
    }

    @Test
    fun `should return project with empty list of auditLogsIds when project created successfully`() {
        val projectName = "Test Project"

        val result = createProjectUseCase(projectName)

        assertThat(result.auditLogsIds).isEmpty()
    }
}
