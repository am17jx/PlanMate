package presentation.screens

import io.mockk.*
import org.example.logic.models.*
import org.example.logic.useCase.DeleteProjectUseCase
import org.example.logic.useCase.GetAllProjectsUseCase
import org.example.logic.useCase.GetEntityAuditLogsUseCase
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.presentation.role.ProjectScreensOptions
import org.example.presentation.screens.ProjectsOverviewUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ProjectsOverviewUITest {
    private lateinit var getAllProjectsUseCase: GetAllProjectsUseCase
    private lateinit var updateProjectUseCase: UpdateProjectUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var getEntityAuditLogsUseCase: GetEntityAuditLogsUseCase
    private lateinit var reader: Reader
    private lateinit var deleteProjectUseCase: DeleteProjectUseCase
    private lateinit var viewer: Viewer
    private lateinit var tablePrinter: TablePrinter
    private lateinit var projectScreensOptions: ProjectScreensOptions

    private val mockOnNavigateToShowProjectTasksUI = mockk<(String) -> Unit>(relaxed = true)
    private val mockOnNavigateToProjectStatusUI = mockk<(String) -> Unit>(relaxed = true)
    private val mockOnNavigateBack = mockk<() -> Unit>(relaxed = true)
    private val mockOnNavigateToExit = mockk<() -> Unit>(relaxed = true)

    private val sampleProjects =
        listOf(
            Project("1", "Project Alpha", states = listOf(State("1", "State Alpha")), auditLogsIds = listOf()),
            Project("2", "Project Beta", states = listOf(State("1", "State Alpha")), auditLogsIds = listOf()),
        )

    private fun launchUI() {
        ProjectsOverviewUI(
            onNavigateToShowProjectTasksUI = mockOnNavigateToShowProjectTasksUI,
            onNavigateToProjectStatusUI = mockOnNavigateToProjectStatusUI,
            onNavigateBack = mockOnNavigateBack,
            getAllProjectsUseCase = getAllProjectsUseCase,
            updateProjectUseCase = updateProjectUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            getEntityAuditLogsUseCase = getEntityAuditLogsUseCase,
            reader = reader,
            viewer = viewer,
            deleteProjectUseCase = deleteProjectUseCase,
            tablePrinter = tablePrinter,
            projectScreensOptions = projectScreensOptions,
            onNavigateToExit = mockOnNavigateToExit,
        )
    }

    @BeforeEach
    fun setUp() {
        getAllProjectsUseCase = mockk()
        updateProjectUseCase = mockk(relaxed = true)
        getProjectByIdUseCase = mockk()
        getEntityAuditLogsUseCase = mockk()
        reader = mockk()
        viewer = mockk(relaxed = true)
        projectScreensOptions = mockk()
        deleteProjectUseCase = mockk()
        tablePrinter = mockk(relaxed = true)

        every { projectScreensOptions.showAllProjectsOptions() } returns
            mapOf(
                "1" to "1 - Show Project Details",
                "5" to "5 - Back",
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
    fun `should return navigation to tasks screen when user chooses to show project details`() {
        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("1", "123", "5")

        launchUI()

        verify { mockOnNavigateToShowProjectTasksUI("123") }
    }

    @Test
    fun `should return updated project when user changes project name`() {
        val projectId = "1"
        val newName = "New Project"
        val existingProject = sampleProjects.first()

        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("2", "1", projectId, newName, "5")
        coEvery { getProjectByIdUseCase(projectId) } returns existingProject

        launchUI()

        coVerify { updateProjectUseCase(existingProject.copy(name = newName)) }
    }

    @Test
    fun `should return navigation to project status screen when user chooses to manage status`() {
        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("2", "2", "42", "5")

        launchUI()

        verify { mockOnNavigateToProjectStatusUI("42") }
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
        val logs =
            listOf(
                AuditLog(
                    id = "1",
                    userId = "user123",
                    action = "Created project",
                    timestamp = 1672531200000,
                    entityType = AuditLogEntityType.PROJECT,
                    entityId = "1",
                    actionType = AuditLogActionType.CREATE,
                ),
            )

        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("4", "1", "5")
        coEvery { getEntityAuditLogsUseCase("1", AuditLogEntityType.PROJECT) } returns logs

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
    fun `should return to previous screen when back is selected`() {
        coEvery { getAllProjectsUseCase() } returns sampleProjects
        every { reader.readString() } returnsMany listOf("5", "0")

        launchUI()

        verify { mockOnNavigateBack() }
    }
}
