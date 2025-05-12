package presentation.screens

import io.mockk.*
import mockdata.createProject
import mockdata.createState
import org.example.logic.models.Project
import org.example.logic.useCase.*
import org.example.presentation.screens.ProjectStateUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProjectStateUITest {

    private lateinit var createProjectStateUseCase: CreateProjectStateUseCase
    private lateinit var updateProjectStateUseCase: UpdateProjectStateUseCase
    private lateinit var deleteProjectStateUseCase: DeleteProjectStateUseCase
    private lateinit var getProjectStatesUseCase: GetProjectStatesUseCase
    private lateinit var reader: Reader
    private lateinit var viewer: Viewer
    private val onNavigateBack = mockk<() -> Unit>(relaxed = true)
    private val tablePrinter = mockk<TablePrinter>(relaxed = true)
    private val ids = List(6) { Uuid.random() }
    private val sampleStates = listOf(
        createState(id = ids[1]),
        createState(id = ids[2]),
        createState(id = ids[3]),
    )

    @BeforeEach
    fun setUp() {
        createProjectStateUseCase = mockk(relaxed = true)
        updateProjectStateUseCase = mockk(relaxed = true)
        deleteProjectStateUseCase = mockk(relaxed = true)
        getProjectStatesUseCase = mockk(relaxed = true)
        reader = mockk()
        viewer = mockk(relaxed = true)
    }

    private fun createUI(): ProjectStateUI {
        return ProjectStateUI(
           tablePrinter = tablePrinter,
            getProjectStatesUseCase =  getProjectStatesUseCase,
            createProjectStateUseCase = createProjectStateUseCase,
            updateProjectStateUseCase = updateProjectStateUseCase,
            deleteProjectStateUseCase = deleteProjectStateUseCase,
            viewer = viewer,
            reader = reader,
            projectId = ids[0],
            onNavigateBack = onNavigateBack
        )
    }

    @Test
    fun `should navigate back when user selects back option`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returns "4"

        createUI()

        verify { onNavigateBack() }
    }

    @Test
    fun `should show error message when fetching project fails`() {
        coEvery { getProjectStatesUseCase(ids[0]) } throws RuntimeException("DB error")
        every { reader.readString() } returns "4"

        createUI()

        verify { viewer.display(match { it.contains("Failed to fetch project states") }) }
    }
}
