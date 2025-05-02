package presentation.screens

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.models.Task
import org.example.logic.useCase.GetEntityAuditLogsUseCase
import org.example.logic.useCase.GetStateNameUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.useCase.deleteTask.DeleteTaskUseCase
import org.example.logic.useCase.updateTask.UpdateTaskUseCase
import org.example.presentation.screens.ShowTaskInformation
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class ShowTaskInformationTest {
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var getStateNameUseCase: GetStateNameUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private lateinit var getEntityAuditLogsUseCase: GetEntityAuditLogsUseCase
    private lateinit var viewer: Viewer
    private lateinit var reader: Reader
    private lateinit var showTaskInformation: ShowTaskInformation

    private val sampleTask = Task(
        id = "task-1",
        name = "Old Name",
        stateId = "state-1",
        addedBy = "user-1",
        auditLogsIds = emptyList(),
        projectId = "proj-1"
    )
    val logs = listOf(
        AuditLog(
            id = "log-1",
            userId = "user-1",
            action = "Created Task",
            timestamp = 1682937600000L,
            entityType = AuditLogEntityType.TASK,
            entityId = "task-1",
            actionType = AuditLogActionType.CREATE
        ),
        AuditLog(
            id = "log-2",
            userId = "user-2",
            action = "Updated Task",
            timestamp = 1683024000000L,
            entityType = AuditLogEntityType.TASK,
            entityId = "task-1",
            actionType = AuditLogActionType.UPDATE
        )
    )

    @BeforeEach
    fun setUp() {
        getTaskByIdUseCase = mockk(relaxed = true)
        getStateNameUseCase = mockk(relaxed = true)
        updateTaskUseCase = mockk(relaxed = true)
        deleteTaskUseCase = mockk(relaxed = true)
        getEntityAuditLogsUseCase = mockk(relaxed = true)
        viewer = mockk(relaxed = true)
        reader = mockk(relaxed = true)

        showTaskInformation =
            ShowTaskInformation(
                getTaskByIdUseCase,
                getStateNameUseCase,
                updateTaskUseCase,
                deleteTaskUseCase,
                getEntityAuditLogsUseCase,
                viewer,
                reader
            )
    }

    @Test
    fun `should exit immediately when tasks are displayed and choice is 4`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf("4")

        showTaskInformation.showTaskInformation("task-1")

        verify { viewer.display(any()) }
    }

    @Test
    fun `should return update task flow when choice is 1`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf("1", "New Name", "New State", "4")

        showTaskInformation.showTaskInformation("task-1")

        val expectedUpdated = sampleTask.copy(name = "New Name", stateId = "New State")
        verify { updateTaskUseCase("task-1", expectedUpdated) }
        verify { viewer.display(any()) }
    }

    @Test
    fun `should return delete task flow when choice is 2 `() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf("2", "y")

        showTaskInformation.showTaskInformation("task-1")

        verify { deleteTaskUseCase("task-1") }
        verify { viewer.display(any()) }
    }

    @Test
    fun `should return logs flow when choice is 3`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { getEntityAuditLogsUseCase("task-1", AuditLogEntityType.TASK) } returns logs
        every { reader.readString() } returnsMany listOf("3", "4")

        showTaskInformation.showTaskInformation("task-1")

        verify { viewer.display(any()) }
        logs.forEach { log ->
            verify {
                viewer.display(any())
            }
        }
    }

    @Test
    fun `should display error updating task when updateTaskUseCase throws`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf(
            "1", "New Name", "new-state", "4"
        )
        every { updateTaskUseCase("task-1", any()) } throws RuntimeException("update failure")

        showTaskInformation.showTaskInformation("task-1")

        verify { viewer.display(any()) }
    }

    @Test
    fun `should display error deleting task when deleteTaskUseCase throws`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf("2", "y")
        every { deleteTaskUseCase("task-1") } throws RuntimeException("delete failure")

        showTaskInformation.showTaskInformation("task-1")

        verify { viewer.display(any()) }
    }
    @Test
    fun `should display deletion cancelled task when user enter n`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf("2", "n")

        showTaskInformation.showTaskInformation("task-1")

        verify { viewer.display(any()) }
    }

    @Test
    fun `should display error fetching logs when getEntityAuditLogsUseCase throws`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf("3", "4")
        every { getEntityAuditLogsUseCase("task-1", AuditLogEntityType.TASK) } throws RuntimeException("logs failure")

        showTaskInformation.showTaskInformation("task-1")

        verify { viewer.display(any()) }
    }
    @Test
    fun `should display invalide option when user enter wrong option`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf("5", "4")

        showTaskInformation.showTaskInformation("task-1")

        verify { viewer.display(any()) }
    }

    @Test
    fun `should display no logs message when logs list is empty`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { getEntityAuditLogsUseCase("task-1", AuditLogEntityType.TASK) } returns emptyList()
        every { reader.readString() } returnsMany listOf("3", "4")

        showTaskInformation.showTaskInformation("task-1")


        verify { viewer.display(any()) }
    }

    @Test
    fun `should update task with default values when new name and sate id are blank`() {
        every { getTaskByIdUseCase("task-1") } returns sampleTask
        every { getStateNameUseCase("task-1") } returns "To Do"
        every { reader.readString() } returnsMany listOf(
            "1",
            "",
            "",
            "4"
        )

        showTaskInformation.showTaskInformation("task-1")

        val expectedTask = sampleTask.copy(name = sampleTask.name, stateId = sampleTask.stateId)
        verify { updateTaskUseCase("task-1", expectedTask) }
        verify { viewer.display(any()) }
    }

}