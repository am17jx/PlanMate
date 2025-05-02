package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetTaskByIdUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidInputException
import org.example.logic.utils.TaskNotFoundException
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GetTaskByIdUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var getTaskByIdUseCase: GetTaskByIdUseCase

    @BeforeEach
    fun setUp() {
        taskRepository = mockk(relaxed = true)
        getTaskByIdUseCase = GetTaskByIdUseCase(taskRepository)
    }

    @Test
    fun `should return task by ID when task exists`() {
        val taskID = "1"
        val expectedTask = Task(taskID, "task", "1", "description", emptyList(), "2")
        every { taskRepository.getTaskById(taskID) } returns expectedTask

        val result = getTaskByIdUseCase(taskID)

        assertThat(result).isEqualTo(expectedTask)
    }

    @Test
    fun `should throw TaskNotFoundException when task does not exist`() {
        val taskID = "1"
        every { taskRepository.getTaskById(taskID) } returns null

        assertThrows<TaskNotFoundException> {
            getTaskByIdUseCase(taskID)
        }
    }

    @Test
    fun `should throw BlankInputException when task ID is blank`() {
        val taskID = ""
        assertThrows<BlankInputException> {
            getTaskByIdUseCase(taskID)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["fdd54-fd456", "894116s-45-5-6"])
    fun `should return task when id contains Letters or Digits or hyphens and not contain any special characters`(
        projectId: String
    ) {
        val expectedTask = Task(projectId, "task", "1", "description", emptyList(), "2")
        every { taskRepository.getTaskById(projectId) } returns expectedTask

        val result = getTaskByIdUseCase(projectId)

        assertThat(result).isEqualTo(expectedTask)
    }

    @ParameterizedTest
    @ValueSource(strings = ["45 45 #% &^", "423545@@!"])
    fun `should throw InvalidInputException when id Contains special Characters`(projectId: String) {
        every { taskRepository.getTaskById(projectId) } returns null

        assertThrows<InvalidInputException> {
            getTaskByIdUseCase(projectId)
        }
    }
}