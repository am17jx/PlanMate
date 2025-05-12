package presentation.screens

import io.mockk.*
import kotlinx.datetime.Clock
import org.example.logic.models.AuditLog
import org.example.logic.models.Project
import org.example.logic.useCase.*
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.presentation.role.ProjectScreensOptions
import org.example.presentation.screens.ProjectsOverviewUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProjectsOverviewUITest {
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var getEntityAuditLogsUseCase: GetEntityAuditLogsUseCase
    private lateinit var logoutUseCase: LogoutUseCase
    private lateinit var reader: Reader
    private lateinit var deleteProjectUseCase: DeleteProjectUseCase
    private lateinit var viewer: Viewer
    private lateinit var tablePrinter: TablePrinter
    private lateinit var projectScreensOptions: ProjectScreensOptions

    private val mockOnNavigateToShowProjectTasksUI = mockk<(Uuid) -> Unit>(relaxed = true)
    private val mockOnNavigateToProjectStatusUI = mockk<(Uuid) -> Unit>(relaxed = true)
    private val mockOnLogout = mockk<() -> Unit>(relaxed = true)
    private val mockOnExit = mockk<() -> Unit>(relaxed = true)

    private val sampleProjects =
        listOf(
            Project(Uuid.random(), "Project Alpha"),
            Project(Uuid.random(), "Project Beta"),
        )

    private fun launchUI() {
        ProjectsOverviewUI(
            onNavigateToShowProjectTasksUI = mockOnNavigateToShowProjectTasksUI,
            onNavigateToProjectStatusUI = mockOnNavigateToProjectStatusUI,
            onLogout = mockOnLogout,
            getAllProjectsUseCase = getAllProjectsUseCase,
            updateProjectUseCase = updateProjectUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            getEntityAuditLogsUseCase = getEntityAuditLogsUseCase,
            logoutUseCase = logoutUseCase,
            reader = reader,
            viewer = viewer,
            deleteProjectUseCase = deleteProjectUseCase,
            tablePrinter = tablePrinter,
            projectScreensOptions = projectScreensOptions,
            onExit = mockOnExit,
        )
    }

    @BeforeEach
    fun setUp() {
        getAllProjectsUseCase = mockk(relaxed = true)
        updateProjectUseCase = mockk(relaxed = true)
        getProjectByIdUseCase = mockk(relaxed = true)
        getEntityAuditLogsUseCase = mockk(relaxed = true)
        logoutUseCase = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        viewer = mockk(relaxed = true)
        projectScreensOptions = mockk(relaxed = true)
        deleteProjectUseCase = mockk(relaxed = true)
        tablePrinter = mockk(relaxed = true)

        every { projectScreensOptions.showAllProjectsOptions() } returns
            mapOf(
                "1" to "1 - Show Project Details",
                "5" to "5 - Logout",
            )
    }

    @Test
    fun `should return all projects when list is not empty`() {
        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returns "5"

        launchUI()

        verify { viewer.display(any()) }
    }

    @Test
    fun `should return message when no projects exist`() {
        coEvery { getAllProjectsUseCase() } returns emptyList()
        every { reader.readString() } returns "5"

        launchUI()

        verify { viewer.display(any()) }
    }

    @Test
    fun `should return error message when exception is thrown while loading projects`() {
        coEvery { getAllProjectsUseCase() } throws RuntimeException("DB Failure")
        every { reader.readString() } returns "5"

        launchUI()

        verify { viewer.display(any()) }
    }

    @Test
    fun `should return updated project when user changes project name`() {
        val projectId = Uuid.random()
        val newName = "New Project"
        val existingProject = sampleProjects.first()

        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("2", "1", newName, "5")
        coEvery { getProjectByIdUseCase(projectId) } returns existingProject

        launchUI()

        coVerify { updateProjectUseCase(existingProject.copy(name = newName)) }
    }

    @Test
    fun `should return invalid input message when user selects unknown update option`() {
        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("2", "999", "5")

        launchUI()

        verify { viewer.display(any()) }
    }

    @Test
    fun `should return deletion confirmation when user deletes project`() {
        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("3", "1", "5")

        launchUI()

        verify { viewer.display(any()) }
    }

    @Test
    fun `should return project logs when user chooses to view them`() {
        val id = Uuid.random()
        val logs =
            listOf(
                AuditLog(
                    id = id,
                    userId = Uuid.random(),
                    createdAt = Clock.System.now(),
                    entityType = AuditLog.EntityType.PROJECT,
                    entityId = id,
                    actionType = AuditLog.ActionType.CREATE,
                    userName = "user123",
                    entityName = "Project Alpha",
                    fieldChange = null,
                ),
            )

        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("4", "1", "5")
        coEvery { getEntityAuditLogsUseCase(id, AuditLog.EntityType.PROJECT) } returns logs

        launchUI()

        verify { viewer.display(any()) }
    }

    @Test
    fun `should return invalid input message when user enters non-numeric main option`() {
        coEvery { getAllProjectsUseCase() } returns sampleProjects
        coEvery { reader.readString() } returnsMany listOf("invalid", "5")

        launchUI()

        verify { viewer.display(any()) }
    }

    @Test
    fun `should logout when user chooses to logout`() {
        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("5", "0")

        launchUI()

        verify { mockOnLogout() }
    }
}
