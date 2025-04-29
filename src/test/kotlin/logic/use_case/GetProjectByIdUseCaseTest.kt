package logic.use_case

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.utils.InvalidInputException
import org.example.logic.utils.ProjectNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GetProjectByIdUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private val project = Project(
        id = "1",
        name = "spacecraft work",
        states = listOf(
            State(id = "state-001", title = "Design Phase"),
            State(id = "state-002", title = "Prototype Build"),
        ),
        auditLogsIds = listOf(
            "audit-1001",
            "audit-1002",
        )
    )

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        getProjectByIdUseCase = GetProjectByIdUseCase(projectRepository)
    }

    @Test
    fun `should return project when pass valid project id`() {
        val projectId = "1"
        every { projectRepository.getProjectById(projectId) } returns project
        val result = getProjectByIdUseCase(projectId)
        assertThat(result).isEqualTo(project)
    }

    @Test
    fun `should throw ProjectNotFoundException when pass valid project id but not existing at data base`() {
        val projectId = "90"
        every { projectRepository.getProjectById(projectId) } returns null
        val result = getProjectByIdUseCase(projectId)
        assertThrows<ProjectNotFoundException> { result }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "423545@@!"])
    fun `should throw InvalidInputException when pass invalid project id`(projectId : String) {
        every { projectRepository.getProjectById(projectId) } returns null
        val result = getProjectByIdUseCase(projectId)
        assertThrows<InvalidInputException> { result }
    }


}