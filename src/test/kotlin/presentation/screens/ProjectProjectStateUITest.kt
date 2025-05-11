package presentation.screens

import io.mockk.*
import org.example.logic.models.Project
import org.example.logic.models.ProjectState
import org.example.logic.useCase.CreateProjectStateUseCase
import org.example.logic.useCase.DeleteProjectStateUseCase
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.UpdateProjectStateUseCase
import org.example.presentation.screens.ProjectStateUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ProjectProjectStateUITest {

    private lateinit var createProjectStateUseCase: CreateProjectStateUseCase
    private lateinit var updateProjectStateUseCase: UpdateProjectStateUseCase
    private lateinit var deleteProjectStateUseCase: DeleteProjectStateUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var reader: Reader
    private lateinit var viewer: Viewer
    private val onNavigateBack = mockk<() -> Unit>(relaxed = true)
    private val tablePrinter = mockk<TablePrinter>(relaxed = true)

    private val sampleProject = Project(
        id = "1",
        name = "Test Project",
        states = listOf(ProjectState("1", "To Do")),
        auditLogsIds = listOf()
    )

    @BeforeEach
    fun setUp() {
        createProjectStateUseCase = mockk(relaxed = true)
        updateProjectStateUseCase = mockk(relaxed = true)
        deleteProjectStateUseCase = mockk(relaxed = true)
        getProjectByIdUseCase = mockk()
        reader = mockk()
        viewer = mockk(relaxed = true)
    }

    private fun createUI(): ProjectStateUI {
        return ProjectStateUI(
           tablePrinter = tablePrinter,
            getProjectByIdUseCase =  getProjectByIdUseCase,
            createProjectStateUseCase = createProjectStateUseCase,
            updateProjectStateUseCase = updateProjectStateUseCase,
            deleteProjectStateUseCase = deleteProjectStateUseCase,
            viewer = viewer,
            reader = reader,
            projectId = "1",
            onNavigateBack = onNavigateBack
        )
    }


    @Test
    fun `should navigate back when user selects back option`() {
        coEvery { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returns "4"

        createUI()

        verify { onNavigateBack() }
    }





    @Test
    fun `should show error message when fetching project fails`() {
        coEvery { getProjectByIdUseCase("1") } throws RuntimeException("DB error")
        every { reader.readString() } returns "4"

        createUI()

        verify { viewer.display(match { it.contains("Failed to fetch project states") }) }
    }




}
