package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.test.runTest
import mockdata.createTask
import org.example.logic.models.AuditLog
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.UpdateTaskUseCase
import org.example.logic.utils.TaskNotChangedException
import org.example.logic.utils.TaskNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class UpdateTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var updateTaskUseCase: UpdateTaskUseCase

    private val taskId = Uuid.random()

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        createAuditLogUseCase = mockk(relaxed = true)
        updateTaskUseCase = UpdateTaskUseCase(taskRepository, createAuditLogUseCase)
    }

    @Test
    fun `should update task when changes detected and create audit log`() = runTest {
        val existingTask = createTask(taskId, "Old Task")
        val updatedTask = existingTask.copy(name = "New Task")

        coEvery { taskRepository.getTaskById(taskId) } returns existingTask
        coEvery { taskRepository.updateTask(updatedTask) } returns updatedTask

        val result = updateTaskUseCase(updatedTask)

        assertThat(result).isEqualTo(updatedTask)
        coVerify(exactly = 1) { createAuditLogUseCase.logUpdate(
            entityType = AuditLog.EntityType.TASK,
            entityId = taskId,
            entityName = "New Task",
            fieldChange = match { it.fieldName == "name" && it.oldValue == "Old Task" && it.newValue == "New Task" }
        ) }
    }

    @Test
    fun `should throw TaskNotFoundException when task does not exist`() = runTest {
        val updatedTask = createTask(taskId, "New Task")
        coEvery { taskRepository.getTaskById(taskId) } returns null

        assertThrows<TaskNotFoundException> {
            updateTaskUseCase(updatedTask)
        }

        coVerify(exactly = 0) { taskRepository.updateTask(any()) }
        coVerify(exactly = 0) { createAuditLogUseCase.logUpdate(any(), any(), any(), any()) }
    }

    @Test
    fun `should throw TaskNotChangedException when task has no changes`() = runTest {
        val existingTask = createTask(taskId, "Same Task")
        val updatedTask = existingTask.copy() // No change

        coEvery { taskRepository.getTaskById(taskId) } returns existingTask

        assertThrows<TaskNotChangedException> {
            updateTaskUseCase(updatedTask)
        }

        coVerify(exactly = 0) { taskRepository.updateTask(any()) }
        coVerify(exactly = 0) { createAuditLogUseCase.logUpdate(any(), any(), any(), any()) }
    }
}
