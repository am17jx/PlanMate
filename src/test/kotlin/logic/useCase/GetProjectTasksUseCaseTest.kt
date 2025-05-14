package logic.useCase

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.utils.NoTasksFoundException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class GetProjectTasksUseCaseTest {
    private lateinit var taskRepository: TaskRepository
    private lateinit var getProjectTasksUseCase: GetProjectTasksUseCase

    private val projectId = Uuid.random()
    private val otherProjectId = Uuid.random()
    private val tasks = listOf(
        Task(
            id = Uuid.random(),
            name = "Task 1",
            stateId = Uuid.random(),
            stateName = "To Do",
            projectId = projectId,
            addedById = Uuid.random(),
            addedByName = "User 1"
        ),
        Task(
            id = Uuid.random(),
            name = "Task 2",
            stateId = Uuid.random(),
            stateName = "In Progress",
            projectId = projectId,
            addedById = Uuid.random(),
            addedByName = "User 1"
        ),
        Task(
            id = Uuid.random(),
            name = "Task 3",
            stateId = Uuid.random(),
            stateName = "Done",
            projectId = otherProjectId,
            addedById = Uuid.random(),
            addedByName = "User 2"
        )
    )

    @BeforeEach
    fun setUp() {
        taskRepository = mockk()
        getProjectTasksUseCase = GetProjectTasksUseCase(taskRepository)
    }

    @Test
    fun `should return only tasks for the specified project when tasks exist`() = runTest {
   
        coEvery { taskRepository.getAllTasks() } returns tasks

       
        val result = getProjectTasksUseCase(projectId)

       
        assertEquals(2, result.size)
        result.forEach { task ->
            assertEquals(projectId, task.projectId)
        }
        coVerify(exactly = 1) { taskRepository.getAllTasks() }
    }

    @Test
    fun `should return empty list when no tasks exist for the project`() = runTest {
   
        coEvery { taskRepository.getAllTasks() } returns tasks

       
        val result = getProjectTasksUseCase(Uuid.random())

       
        assertEquals(emptyList(), result)
        coVerify(exactly = 1) { taskRepository.getAllTasks() }
    }

    @Test
    fun `should return empty list when no tasks exist at all`() = runTest {
   
        coEvery { taskRepository.getAllTasks() } returns emptyList()

       
        val result = getProjectTasksUseCase(projectId)

       
        assertEquals(emptyList(), result)
        coVerify(exactly = 1) { taskRepository.getAllTasks() }
    }

    @Test
    fun `should propagate NoTasksFoundException from repository`() = runTest {
   
        coEvery { taskRepository.getAllTasks() } throws NoTasksFoundException()

        
        assertThrows<NoTasksFoundException> {
            getProjectTasksUseCase(projectId)
        }
        coVerify(exactly = 1) { taskRepository.getAllTasks() }
    }

    @Test
    fun `should handle repository runtime exceptions`() = runTest {
   
        val errorMessage = "Database connection failed"
        coEvery { taskRepository.getAllTasks() } throws RuntimeException(errorMessage)

       
        val exception = assertThrows<RuntimeException> {
            getProjectTasksUseCase(projectId)
        }
        assertEquals(errorMessage, exception.message)
        coVerify(exactly = 1) { taskRepository.getAllTasks() }
    }
}
