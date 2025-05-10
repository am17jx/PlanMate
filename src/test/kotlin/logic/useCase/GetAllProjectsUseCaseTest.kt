package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.Project
import org.example.logic.models.State
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
            id = "1", name = "Spacecraft Work", tasksStatesIds = listOf(
                State(id = "state-001", title = "Design Phase"), State(id = "state-002", title = "Prototype Build")
            ), auditLogsIds = listOf(
                "audit-1001", "audit-1002"
            )
        ), Project(
            id = "2", name = "Mars Rover Development", tasksStatesIds = listOf(
                State(id = "state-003", title = "Initial Research"),
                State(id = "state-004", title = "Component Testing"),
                State(id = "state-005", title = "Final Assembly")
            ), auditLogsIds = listOf(
                "audit-2001", "audit-2002", "audit-2003"
            )
        ), Project(
            id = "3", name = "Satellite Deployment", tasksStatesIds = emptyList(), auditLogsIds = listOf(
                "audit-3001"
            )
        ), Project(
            id = "4", name = "Empty Project", tasksStatesIds = emptyList(), auditLogsIds = emptyList()
        ), Project(
            id = "5", name = "Lunar Base Planning", tasksStatesIds = listOf(
                State(id = "state-006", title = "Site Selection"),
                State(id = "state-007", title = "Resource Mapping"),
                State(id = "state-008", title = "Construction Planning"),
                State(id = "state-009", title = "Crew Training")
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