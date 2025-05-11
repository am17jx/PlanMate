package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.GetAllProjectsUseCase
import org.example.logic.utils.NoProjectsFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class GetAllProjectsUseCaseTest {

    private lateinit var projectRepository: ProjectRepository
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private val projects = listOf(
        Project(
            id = "1", name = "Spacecraft Work", projectStateIds = listOf(
                "state-001", "state-002",
            ), auditLogsIds = listOf(
                "audit-1001", "audit-1002"
            )
        ), Project(
            id = "2", name = "Mars Rover Development", projectStateIds = listOf(
                "state-001", "state-002",
            ), auditLogsIds = listOf(
                "audit-2001", "audit-2002", "audit-2003"
            )
        ), Project(
            id = "3", name = "Satellite Deployment", projectStateIds = emptyList(), auditLogsIds = listOf(
                "audit-3001"
            )
        ), Project(
            id = "4", name = "Empty Project", projectStateIds = emptyList(), auditLogsIds = emptyList()
        ), Project(
            id = "5", name = "Lunar Base Planning", projectStateIds = listOf(
                "state-001", "state-002",
            ), auditLogsIds = listOf(
                "audit-4001", "audit-4002", "audit-4003", "audit-4004", "audit-4005"
            )
        )
    )

    @BeforeEach
    fun setUp() {
        projectRepository = mockk(relaxed = true)
        getAllProjectsUseCase = GetAllProjectsUseCase(projectRepository)
    }


    @Test
    fun `should return all projects when found projects at file`() = runTest {
        coEvery { projectRepository.getAllProjects() } returns projects

        assertThat(getAllProjectsUseCase()).isEqualTo(projects)
    }

    @Test
    fun `should throw NoProjectsFoundException when no projects found`() = runTest {
        coEvery { projectRepository.getAllProjects() } returns emptyList()

        assertThrows<NoProjectsFoundException> { getAllProjectsUseCase() }

    }
}