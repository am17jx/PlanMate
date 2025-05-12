package presentation.screens

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import logic.useCase.CreateTaskUseCase
import mockdata.createProject
import mockdata.createState
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
    fun `should navigate back when option 3 is selected`() {
        every { reader.readInt() } returns GO_BACK_OPTION

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
    fun `should navigate to task details when option 2 is selected with valid task ID`() {
        every { reader.readInt() } returnsMany listOf(VIEW_TASK_OPTION, 1)

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

        assertThat(navigatedTaskId).isNotNull()
        verify { viewer.display("Enter task index: ") }
    }

    @Test
    fun `should handle invalid task ID when option 2 is selected`() {
        val invalidTaskIndex = 999
        val validTaskIndex = 1
        every { reader.readInt() } returnsMany listOf(VIEW_TASK_OPTION, invalidTaskIndex, validTaskIndex)

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

        verify { viewer.display("Invalid index. Please try again.") }
        verify(exactly = 2) { viewer.display("Enter task index: ") }
        assertThat(navigatedTaskId).isNotNull()
    }

    @Test
    fun `should not start create new task flow when project has no states`()= runTest {
        coEvery { getProjectByIdUseCase(any()) } returns createProject(id = ids[0], name = "Test Project")
        every { reader.readInt() } returnsMany listOf(GO_BACK_OPTION)
        coEvery { getProjectStatesUseCase(any()) } returns emptyList()
        coEvery { createTaskUseCase.invoke(any(), any(), any()) } returns createTask(
            name = "New Task",
            stateId = ids[3]
        )

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
    }


    @Test
    fun `should handle invalid menu option when it is selected`() {
        every { reader.readInt() } returnsMany listOf(99, 3)

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

    companion object {
        private const val CREATE_TASK_OPTION = 1
        private const val VIEW_TASK_OPTION = 2
        private const val GO_BACK_OPTION = 3

    }
}
