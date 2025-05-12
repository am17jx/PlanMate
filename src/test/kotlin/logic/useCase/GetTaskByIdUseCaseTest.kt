package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createTask
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidInputException
import org.example.logic.utils.TaskNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetTaskByIdUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase
    private val ids = List(6) { Uuid.random() }
    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        getTaskByIdUseCase = GetTaskByIdUseCase(taskRepository)
    }

    @Test
    fun `should return task by ID when task exists`() = runTest {
        val taskID = ids[0]
        val expectedTask = createTask(taskID, "task")
        coEvery { taskRepository.getTaskById(taskID) } returns expectedTask

        val result = getTaskByIdUseCase(taskID)

        assertThat(result).isEqualTo(expectedTask)
    }

    @Test
    fun `should throw TaskNotFoundException when task does not exist`() = runTest {
        val taskID = ids[1]
        coEvery { taskRepository.getTaskById(taskID) } returns null

        assertThrows<TaskNotFoundException> {
            getTaskByIdUseCase(taskID)
        }
    }

    @Test
    fun `should throw BlankInputException when task ID is blank`() = runTest {
        val taskID = ids[3]
        assertThrows<BlankInputException> {
            getTaskByIdUseCase(taskID)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["fdd54-fd456", "894116s-45-5-6"])
    fun `should return task when id contains Letters or Digits or hyphens and not contain any special characters`(
        projectId: String
    ) = runTest {
        val projectUuid = Uuid.parseHex(projectId)
        val expectedTask = createTask(projectUuid, "task")
        coEvery { taskRepository.getTaskById(projectUuid) } returns expectedTask

        val result = getTaskByIdUseCase(projectUuid)

        assertThat(result).isEqualTo(expectedTask)
    }

    @ParameterizedTest
    @ValueSource(strings = ["45 45 #% &^", "423545@@!"])
    fun `should throw InvalidInputException when id Contains special Characters`(projectId: String) = runTest {
        val projectUuid = Uuid.parseHex(projectId)
        coEvery { taskRepository.getTaskById(projectUuid) } returns null

        assertThrows<InvalidInputException> {
            getTaskByIdUseCase(projectUuid)
        }
    }
}