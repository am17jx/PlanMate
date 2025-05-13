package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.AuditLog
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.DeleteTaskUseCase
import org.example.logic.useCase.GetTaskByIdUseCase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DeleteTaskUseCaseTest {

    private lateinit var taskRepository: TaskRepository
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private lateinit var createAuditLogUseCase: CreateAuditLogUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase

    private val testTask = Task(
        id = Uuid.random(),
        name = "Test Task",
        stateId = Uuid.random(),
        stateName = "To Do",
        addedById = Uuid.random(),
        addedByName = "Test User",
        projectId = Uuid.random()
    )

    @BeforeTest
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        getTaskByIdUseCase = mockk()
        createAuditLogUseCase = mockk(relaxed = true)

        deleteTaskUseCase = DeleteTaskUseCase(
            taskRepository = taskRepository,
            getTaskByIdUseCase = getTaskByIdUseCase,
            createAuditLogUseCase = createAuditLogUseCase
        )

        coEvery { getTaskByIdUseCase(testTask.id) } returns testTask
    }

    @Test
    fun `should delete task and create audit log`() = runTest {
        deleteTaskUseCase(testTask.id)

        coVerify { taskRepository.deleteTask(testTask.id) }

        coVerify {
            createAuditLogUseCase.logDeletion(
                entityType = AuditLog.EntityType.TASK,
                entityId = testTask.id,
                entityName = testTask.name
            )
        }
    }
}