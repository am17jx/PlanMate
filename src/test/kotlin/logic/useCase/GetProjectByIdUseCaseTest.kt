package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidInputException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetProjectByIdUseCaseTest {
    private lateinit var projectRepository: ProjectRepository
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private val project =
        Project(
            id = "123456",
            name = "spacecraft work",
            states =
                listOf(
                    State(id = "state-001", title = "Design Phase"),
                    State(id = "state-002", title = "Prototype Build"),
                ),
            auditLogsIds =
                listOf(
                    "audit-1001",
                    "audit-1002",
                ),
        )

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        getProjectByIdUseCase = GetProjectByIdUseCase(projectRepository)
    }

    @Test
    fun `should return project when pass valid id have char and number only`() {
        val projectId = "123456"
        every { projectRepository.getProjectById(projectId) } returns project
        val result = getProjectByIdUseCase(projectId)

        assertThat(result).isEqualTo(project)
    }

    @Test
    fun `should throw BlankInputException when pass blank id`() {
        val projectId = ""
        every { projectRepository.getProjectById(projectId) } returns null

        assertThrows<BlankInputException> {
            getProjectByIdUseCase(projectId)
        }
    }

    @Test
    fun `should throw InvalidInputException when pass invalid id have spical chars`() {
        val projectId = "dasd3!@!@#$#@$"
        every { projectRepository.getProjectById(projectId) } returns null
        assertThrows<InvalidInputException> {
            getProjectByIdUseCase(projectId)
        }
    }
}
