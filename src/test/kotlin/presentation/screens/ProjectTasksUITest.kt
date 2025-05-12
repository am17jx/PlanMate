package presentation.screens

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import logic.useCase.CreateTaskUseCase
import mockdata.createProject
import mockdata.createTask
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.GetProjectStatesUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.utils.ProjectNotFoundException
import org.example.presentation.screens.ProjectTasksUI
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.TablePrinter
import presentation.utils.io.Reader
import presentation.utils.io.Viewer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProjectTasksUITest {
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase
    private lateinit var getProjectByIdUseCase: GetProjectByIdUseCase
    private lateinit var getProjectStatesUseCase: GetProjectStatesUseCase
    private lateinit var createTaskUseCase: CreateTaskUseCase
    private lateinit var reader: Reader
    private lateinit var viewer: Viewer
    private lateinit var tablePrinter: TablePrinter
    private lateinit var projectTasksUi: ProjectTasksUI
    private var isNavigateBackCalled: Boolean = false
    private var navigatedTaskId: Uuid? = null
    private val ids = List(6) { Uuid.random() }
    private val project = createProject(id = ids[0], name = "Test Project")
    private val projectTasks = listOf(
        createTask(id = ids[1], name = "Task 1", projectId = ids[0], stateId = ids[3]),
        createTask(id = ids[2], name = "Task 2", projectId = ids[0], stateId = ids[3])
    )

    @BeforeEach
    fun setUp() {
        getProjectTasksUseCase = mockk(relaxed = true)
        getProjectByIdUseCase = mockk(relaxed = true)
        createTaskUseCase = mockk(relaxed = true)
        getProjectStatesUseCase = mockk(relaxed = true)
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

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        assertThat(isNavigateBackCalled).isTrue()
        verify { viewer.display(match { it.contains("Select Option") }) }
    }

    @Test
    fun `should navigate to task details when option 1 is selected with valid task ID`() {
        every { reader.readInt() } returns 1
        every { reader.readString() } returns "1"

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        assertThat(navigatedTaskId).isEqualTo("1")
        verify { viewer.display("Enter Task Id: ") }
    }

    @Test
    fun `should handle invalid task ID when option 1 is selected`() {
        val invalidTaskId = "999"
        every { reader.readInt() } returnsMany listOf(1, 0)
        every { reader.readString() } returns invalidTaskId

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        verify { viewer.display("Id is incorrect!") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should create new task when option 2 is selected with valid inputs`() {
        every { reader.readInt() } returnsMany listOf(2, 0)
        every { reader.readString() } returnsMany listOf("New Task", "1")
        coEvery { createTaskUseCase.invoke(any(), any(), any()) } returns createTask(name = "New Task", stateId = ids[4])

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        coVerify { createTaskUseCase.invoke("New Task", ids[0], ids[3]) }
        verify { viewer.display("Enter Task Name: ") }
        verify { viewer.display("Select a state from the following states:") }
        verify { viewer.display("Enter state ID: ") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should not start create new task flow when project has no states`() {
        coEvery { getProjectByIdUseCase(any()) } returns createProject(id = ids[0], name = "Test Project")
        every { reader.readInt() } returnsMany listOf(2, 0)
        coEvery { createTaskUseCase.invoke(any(), any(), any()) } returns createTask(name = "New Task", stateId = ids[3])

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        verify { viewer.display("No project states added yet! Go back and update project with new states.") }
        assertThat(isNavigateBackCalled).isTrue()
    }


    @Test
    fun `should handle invalid menu option when it is selected`() {
        every { reader.readInt() } returnsMany listOf(99, 0)

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        verify { viewer.display("Invalid option. Please, try again!") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should handle project not found exception`() {
        coEvery { getProjectByIdUseCase.invoke(any()) } throws ProjectNotFoundException()

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        verify { viewer.display("Error: project not found") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should handle generic exception when loading tasks`() {
        coEvery { getProjectTasksUseCase.invoke(any()) } throws RuntimeException("Generic error")

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        verify { viewer.display("Generic error") }
    }

    @Test
    fun `should handle exception when creating task`() {
        every { reader.readInt() } returnsMany listOf(2, 0)
        every { reader.readString() } returnsMany listOf("New Task", "1")
        coEvery { createTaskUseCase.invoke(any(), any(), any()) } throws RuntimeException("Error creating task")

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        verify { viewer.display("Error creating task") }
        assertThat(isNavigateBackCalled).isTrue()
    }

    @Test
    fun `should display empty states message when project has no states`() {
        val projectWithNoStates = createProject(id = ids[0], name = "Test Project")
        coEvery { getProjectByIdUseCase.invoke(any()) } returns projectWithNoStates

        projectTasksUi = ProjectTasksUI(
            getProjectTasksUseCase = getProjectTasksUseCase,
            getProjectByIdUseCase = getProjectByIdUseCase,
            createTaskUseCase = createTaskUseCase,
            getProjectStatesUseCase = getProjectStatesUseCase,
            reader = reader,
            viewer = viewer,
            tablePrinter = tablePrinter,
            onNavigateBack = {
                isNavigateBackCalled = true
            },
            onNavigateToTaskDetails = { taskId ->
                navigatedTaskId = taskId
            },
            projectId = ids[0]
        )

        verify { viewer.display("<==========( No States Added yet )==========>") }
    }
}
