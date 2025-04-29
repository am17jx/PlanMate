package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.TaskNotFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

class GetProjectTasksUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase
    private val dummyTasks =
        listOf(
            Task("1", "TaskName", "state", "potatoMan", emptyList(), "123"),
            Task("2", "TaskName", "state", "potatoMan", emptyList(), "123"),
            Task("3", "TaskName", "state", "potatoMan", emptyList(), "789"),
        )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        getProjectTasksUseCase = GetProjectTasksUseCase(taskRepository)
    }

    @ParameterizedTest
    @MethodSource("noTaskForProjectId")
    fun `should throw TaskNotFoundException when no tasks are found for the given project id`(tasks: List<Task>) {
        val projectId = "eq1wa"
        every { taskRepository.getAllTasks() } returns tasks

        assertThrows<TaskNotFoundException> {
            getProjectTasksUseCase(projectId)
        }
    }

    @Test
    fun `should throw BlankInputException when given blank project id`() {
        val projectId = ""
        every { taskRepository.getAllTasks() } returns dummyTasks

        assertThrows<BlankInputException> {
            getProjectTasksUseCase(projectId)
        }
    }

    @Test
    fun `should return a list of tasks for the given project id`() {
        val projectId = "123"
        every { taskRepository.getAllTasks() } returns dummyTasks

        val result = getProjectTasksUseCase(projectId)

        assertThat(result).isNotEmpty()
        assertThat(result.map { it.projectId }.equals(projectId)).isTrue()
    }

    companion object {
        @JvmStatic
        fun noTaskForProjectId(): Stream<Arguments> =
            Stream.of(
                Arguments.argumentSet(
                    "when return empty list",
                    emptyList<Task>(),
                ),
                Arguments.argumentSet(
                    "when return list that doesn't contain the given project id",
                    listOf(
                        Task("1", "TaskName", "state", "potatoMan", emptyList(), "123"),
                        Task("2", "TaskName", "state", "potatoMan", emptyList(), "123"),
                        Task("3", "TaskName", "state", "potatoMan", emptyList(), "789"),
                    ),
                ),
            )
    }
}
