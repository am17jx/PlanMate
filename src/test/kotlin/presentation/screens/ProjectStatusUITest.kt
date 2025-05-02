package presentation.screens

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.useCase.CreateStateUseCase
import org.example.logic.useCase.DeleteStateUseCase
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.UpdateStateUseCase
import org.example.presentation.screens.ProjectStatusUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ProjectStatusUITest {

    private lateinit var createStateUseCase: CreateStateUseCase
    private lateinit var updateStateUseCase: UpdateStateUseCase
    private lateinit var deleteStateUseCase: DeleteStateUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var reader: Reader
    private lateinit var viewer: Viewer
    private val onNavigateBack = mockk<() -> Unit>(relaxed = true)

    private val sampleProject = Project(
        id = "1",
        name = "Test Project",
        states = listOf(State("1", "To Do")),
        auditLogsIds = listOf()
    )

    @BeforeEach
    fun setUp() {
        createStateUseCase = mockk(relaxed = true)
        updateStateUseCase = mockk(relaxed = true)
        deleteStateUseCase = mockk(relaxed = true)
        getProjectByIdUseCase = mockk()
        reader = mockk()
        viewer = mockk(relaxed = true)
    }

    private fun createUI(): ProjectStatusUI {
        return ProjectStatusUI(
            createStateUseCase,
            updateStateUseCase,
            deleteStateUseCase,
            getProjectByIdUseCase,
            "1",
            viewer,
            reader,
            onNavigateBack
        )
    }

    @Test
    fun `should create state when user selects create option`() {
        every { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returnsMany listOf("1", "New State", "4")

        createUI()

        verify { createStateUseCase("1", "New State") }
        verify { viewer.display("State created successfully.") }
    }

    @Test
    fun `should update state when user selects update option`() {
        every { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returnsMany listOf("2", "1", "Updated State", "4")

        createUI()

        verify { updateStateUseCase("Updated State", "1", "1") }
        verify { viewer.display("State updated successfully.") }
    }

    @Test
    fun `should delete state when user selects delete option`() {
        every { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returnsMany listOf("3", "1", "4")

        createUI()

        verify { deleteStateUseCase("1", "1") }
        verify { viewer.display("State deleted successfully.") }
    }

    @Test
    fun `should navigate back when user selects back option`() {
        every { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returns "4"

        createUI()

        verify { onNavigateBack() }
    }

    @Test
    fun `should show invalid input message when user enters unexpected value`() {
        every { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returnsMany listOf("invalid", "4")

        createUI()

        verify { viewer.display("Invalid input. Please try again.") }
    }

    @Test
    fun `should show no states message when project has no states`() {
        val projectWithNoStates = sampleProject.copy(states = listOf())
        every { getProjectByIdUseCase("1") } returns projectWithNoStates
        every { reader.readString() } returns "4"

        createUI()

        verify { viewer.display("No states found for this project.") }
    }

    @Test
    fun `should show error message when fetching project fails`() {
        every { getProjectByIdUseCase("1") } throws RuntimeException("DB error")
        every { reader.readString() } returns "4"

        createUI()

        verify { viewer.display(match { it.contains("Failed to fetch project states") }) }
    }

    @Test
    fun `should show error message when state creation fails`() {
        every { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returnsMany listOf("1", "Buggy State", "4")
        every { createStateUseCase("1", "Buggy State") } throws RuntimeException("API failed")

        createUI()

        verify { viewer.display("Failed to create state: API failed") }
    }

    @Test
    fun `should show error message when state update fails`() {
        every { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returnsMany listOf("2", "1", "Crash", "4")
        every { updateStateUseCase("Crash", "1", "1") } throws RuntimeException("Update error")

        createUI()

        verify { viewer.display("Failed to update state: Update error") }
    }

    @Test
    fun `should show error message when state deletion fails`() {
        every { getProjectByIdUseCase("1") } returns sampleProject
        every { reader.readString() } returnsMany listOf("3", "1", "4")
        every { deleteStateUseCase("1", "1") } throws RuntimeException("Delete error")

        createUI()

        verify { viewer.display("Failed to delete state: Delete error") }
    }
}
