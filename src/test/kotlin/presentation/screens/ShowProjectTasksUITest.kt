package presentation.screens

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import logic.useCase.CreateTaskUseCase
import mockdata.createProject
import mockdata.createTask
import org.example.logic.models.State
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.utils.ProjectNotFoundException
import org.example.presentation.screens.ShowProjectTasksUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ShowProjectTasksUITest {
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var createTaskUseCase: CreateTaskUseCase
    private lateinit var reader: Reader
    private lateinit var viewer: Viewer
    private lateinit var tablePrinter: TablePrinter
    private lateinit var showProjectTasksUi: ShowProjectTasksUI
    private var isNavigateBackCalled: Boolean = false
    private var navigatedTaskId: String? = null

    private val project =
        createProject(id = "1", name = "Test Project", states = listOf(State(id = "1", title = "State 1")))
    private val projectTasks = listOf(
        createTask(id = "1", name = "Task 1", projectId = "1", stateId = "1"),
        createTask(id = "2", name = "Task 2", projectId = "1", stateId = "1")
    )

    @BeforeEach
    fun setUp() {
        getProjectTasksUseCase = mockk(relaxed = true)
        getProjectByIdUseCase = mockk(relaxed = true)
        createTaskUseCase = mockk(relaxed = true)
        reader = mockk(relaxed = true)
        viewer = mockk(relaxed = true)
        tablePrinter = mockk(relaxed = true)
        isNavigateBackCalled = false
        navigatedTaskId = null

        coEvery { getProjectByIdUseCase.invoke(any()) } returns project
        coEvery { getProjectTasksUseCase.invoke(any()) } returns projectTasks
    }



    @Test
    fun `should navigate back when option 0 is selected`() {
        every { reader.readInt() } returns 0

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        assertThat(isNavigateBackCalled).isTrue()
        verify { viewer.display(match { it.contains("Select Option") }) }
    }

    @Test
    fun `should navigate to task details when option 1 is selected with valid task ID`() {
        every { reader.readInt() } returns 1
        every { reader.readString() } returns "1"

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        assertThat(navigatedTaskId).isEqualTo("1")
        verify { viewer.display("Enter Task Id: ") }
    }

    @Test
    fun `should handle invalid task ID when option 1 is selected`() {
        val invalidTaskId = "999"
        every { reader.readInt() } returnsMany listOf(1, 0)
        every { reader.readString() } returns invalidTaskId

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        verify { viewer.display("Id is incorrect!") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should create new task when option 2 is selected with valid inputs`() {
        every { reader.readInt() } returnsMany listOf(2, 0)
        every { reader.readString() } returnsMany listOf("New Task", "1")
        coEvery { createTaskUseCase.invoke(any(), any(), any()) } returns createTask(name = "New Task", stateId = "1")

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        coVerify { createTaskUseCase.invoke("New Task", "1", "1") }
        verify { viewer.display("Enter Task Name: ") }
        verify { viewer.display("Select a state from the following states:") }
        verify { viewer.display("Enter state ID: ") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should not start create new task flow when project has no states`() {
        coEvery { getProjectByIdUseCase(any()) } returns createProject(id = "1", name = "Test Project")
        every { reader.readInt() } returnsMany listOf(2, 0)
        coEvery { createTaskUseCase.invoke(any(), any(), any()) } returns createTask(name = "New Task", stateId = "1")

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        verify { viewer.display("No project states added yet! Go back and update project with new states.") }
        assertThat(isNavigateBackCalled).isTrue()
    }



    @Test
    fun `should handle invalid menu option when it is selected`() {
        every { reader.readInt() } returnsMany listOf(99, 0)

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        verify { viewer.display("Invalid option. Please, try again!") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should handle project not found exception`() {
        coEvery { getProjectByIdUseCase.invoke(any()) } throws ProjectNotFoundException()

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        verify { viewer.display("Error: project not found") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should handle generic exception when loading tasks`() {
        coEvery { getProjectTasksUseCase.invoke(any()) } throws RuntimeException("Generic error")

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        verify { viewer.display("Generic error") }
    }

    @Test
    fun `should handle exception when creating task`() {
        every { reader.readInt() } returnsMany listOf(2, 0)
        every { reader.readString() } returnsMany listOf("New Task", "1")
        coEvery { createTaskUseCase.invoke(any(), any(), any()) } throws RuntimeException("Error creating task")

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        verify { viewer.display("Error creating task") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should display empty states message when project has no states`() {
        val projectWithNoStates = createProject(id = "1", name = "Test Project", states = emptyList())
        coEvery { getProjectByIdUseCase.invoke(any()) } returns projectWithNoStates

        showProjectTasksUi = ShowProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = "1"
        )

        verify { viewer.display("<==========( No States Added yet )==========>") }
    }
}
