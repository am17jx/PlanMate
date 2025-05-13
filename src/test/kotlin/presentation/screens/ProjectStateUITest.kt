package presentation.screens

import io.mockk.*
import mockdata.createState
import org.example.logic.useCase.CreateProjectStateUseCase
import org.example.logic.useCase.DeleteProjectStateUseCase
import org.example.logic.useCase.GetProjectStatesUseCase
import org.example.logic.useCase.UpdateProjectStateUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskStateNotFoundException
import org.example.presentation.screens.ProjectStateUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
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
    private val sampleStates =
        listOf(
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

    private fun createUI(): ProjectStateUI =
        ProjectStateUI(
            tablePrinter = tablePrinter,
            getProjectStatesUseCase = getProjectStatesUseCase,
            createProjectStateUseCase = createProjectStateUseCase,
            updateProjectStateUseCase = updateProjectStateUseCase,
            deleteProjectStateUseCase = deleteProjectStateUseCase,
            viewer = viewer,
            reader = reader,
            projectId = ids[0],
            onNavigateBack = onNavigateBack,
        )

    // On creating
    @Test
    fun `should create project state when user selects create option`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("1", "New State", "4")

        createUI()

        coVerify { createProjectStateUseCase(ids[0], "New State") }
        verify { viewer.display(match { it.contains("State created successfully") }) }
    }

    @Test
    fun `should show error message when fails to find the project on creating state`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("1", "New State", "4")
        coEvery { createProjectStateUseCase(ids[0], "New State") } throws ProjectNotFoundException()

        createUI()

        verify { viewer.display(match { it.contains("Failed to create state") }) }
    }

    @Test
    fun `should show error message when given blank state name on creating state`() {
        val blankStateName = ""
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("1", blankStateName, "4")
        coEvery { createProjectStateUseCase(ids[0], blankStateName) } throws BlankInputException()

        createUI()

        verify { viewer.display(match { it.contains("Input cannot be blank") }) }
    }

    @Test
    fun `should show error message when general exception occurs on creating state`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("1", "New State", "4")
        coEvery { createProjectStateUseCase(ids[0], "New State") } throws Exception()

        createUI()

        verify { viewer.display(match { it.contains("Failed to create state") }) }
    }

    // On updating
    @Test
    fun `should update project state when user selects update option`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("2", "1", "Update task", "4")

        createUI()

        coVerify { updateProjectStateUseCase("Update task", ids[1], ids[0]) }
        verify { viewer.display(match { it.contains("State updated successfully") }) }
    }

    @Test
    fun `should show error message when fails to find the state on updating state`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("2", "1", "Updated State", "4")
        coEvery { updateProjectStateUseCase(any(), sampleStates[0].id, ids[0]) } throws TaskStateNotFoundException()

        createUI()

        verify { viewer.display(match { it.contains("Failed to update state") }) }
    }

    @Test
    fun `should show error message when fails to find the project on updating state`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("2", "1", "Updated State", "4")
        coEvery { updateProjectStateUseCase(any(), sampleStates[0].id, ids[0]) } throws ProjectNotFoundException()

        createUI()

        verify { viewer.display(match { it.contains("Failed to update state") }) }
    }

    @Test
    fun `should show error message when given blank state name on updating state`() {
        val blankStateName = ""
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("2", "1", blankStateName, "4")
        coEvery { updateProjectStateUseCase(sampleStates[0].title, sampleStates[0].id, ids[0]) } throws BlankInputException()

        createUI()

        verify { viewer.display(match { it.contains("Input cannot be blank") }) }
    }

    @Test
    fun `should show error message when general exception occurs on updating state`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("2", "1", "Updated State", "4")
        coEvery { updateProjectStateUseCase(any(), sampleStates[0].id, ids[0]) } throws Exception()

        createUI()

        verify { viewer.display(match { it.contains("Failed to update state") }) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["5", "a", ""])
    fun `should show error message when given invalid index on updating state`(input: String) {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("2", input, "Updated State", "4")

        createUI()

        verify { viewer.display(match { it.contains("Invalid index. Please try again") }) }
    }

    // On deleting
    @Test
    fun `should delete project state when user selects delete option`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("3", "1", "4")

        createUI()

        coVerify { deleteProjectStateUseCase(ids[1], ids[0]) }
        verify { viewer.display(match { it.contains("State deleted successfully") }) }
    }

    @Test
    fun `should show error message when fails to find the state on deleting state`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("3", "1", "4")
        coEvery { deleteProjectStateUseCase(sampleStates[0].id, ids[0]) } throws TaskStateNotFoundException()

        createUI()

        verify { viewer.display(match { it.contains("Failed to delete state") }) }
    }

    @Test
    fun `should show error message when fails to find the project on deleting state`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("3", "1", "4")
        coEvery { deleteProjectStateUseCase(sampleStates[0].id, ids[0]) } throws ProjectNotFoundException()

        createUI()

        verify { viewer.display(match { it.contains("Failed to delete state") }) }
    }

    @Test
    fun `should show error message when general exception occurs on deleting state`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("3", "1", "4")
        coEvery { deleteProjectStateUseCase(sampleStates[0].id, ids[0]) } throws Exception()

        createUI()

        verify { viewer.display(match { it.contains("Failed to delete state") }) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["5", "a", ""])
    fun `should show error message when given invalid index on deleting state`(input: String) {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("3", input, "4")

        createUI()

        verify { viewer.display(match { it.contains("Invalid index. Please try again") }) }
    }

    @Test
    fun `should show error message when input is invalid`() {
        coEvery { getProjectStatesUseCase(ids[0]) } returns sampleStates
        every { reader.readString() } returnsMany listOf("ew", "4")

        createUI()

        verify { viewer.display(match { it.contains("Invalid input. Please try again") }) }
        verify { onNavigateBack() }
    }

    @Test
    fun `should show error message when no project found`() {
        coEvery { getProjectStatesUseCase(ids[0]) } throws ProjectNotFoundException()
        every { reader.readString() } returns "4"

        createUI()

        verify { viewer.display(match { it.contains("Failed to fetch project states") }) }
    }

    @Test
    fun `should show error message when no project found qwe`() {
        coEvery { getProjectStatesUseCase(ids[0]) } throws ProjectNotFoundException()
        every { reader.readString() } returns "4"

        createUI()

        verify { viewer.display(match { it.contains("Failed to fetch project states") }) }
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
        coEvery { getProjectStatesUseCase(ids[0]) } throws RuntimeException()
        every { reader.readString() } returns "4"

        createUI()

        verify { viewer.display(match { it.contains("Failed to fetch project states") }) }
    }
}
