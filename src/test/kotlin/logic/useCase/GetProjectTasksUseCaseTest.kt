package logic.useCase

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createTask
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.utils.BlankInputException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectTasksUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase
    private val ids = List(6) { Uuid.random() }
    private val dummyTasks = listOf(
        createTask(ids[1], "TaskName"),
        createTask(ids[2], "TaskName"),
        createTask(ids[3], "TaskName"),
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        getProjectTasksUseCase = GetProjectTasksUseCase(taskRepository)
    }

    @Test
    fun `should throw BlankInputException when given blank project id`() = runTest {
        val projectId = ids[2]
        coEvery { taskRepository.getAllTasks() } returns dummyTasks

        assertThrows<BlankInputException> {
            getProjectTasksUseCase(projectId)
        }
    }

    @Test
    fun `should return a list of tasks for the given project id`() = runTest {
        val projectId = ids[0]
        coEvery { taskRepository.getAllTasks() } returns dummyTasks

        val result = getProjectTasksUseCase(projectId)

        assertThat(result).isNotEmpty()
        result.forEach {
            assertThat(it.projectId).isEqualTo(projectId)
        }
    }
}
