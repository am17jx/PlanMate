package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createTask
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.source.remote.contract.RemoteTaskDataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskRepositoryImplTest {
    private lateinit var mockRemoteDataSource: RemoteTaskDataSource
    private lateinit var taskRepositoryImpl: TaskRepositoryImpl

    // Test data
    private val testTask = createTask(
        id = "task-1",
        name = "Test Task",
    )

    private val updatedTask = testTask.copy(
        name = "Updated Task", stateId = "state-2"
    )

    private val taskList = listOf(
        testTask, createTask(
            id = "task-2",
            name = "Task 2",
        )
    )

    @BeforeEach
    fun setUp() {
        mockRemoteDataSource = mockk(relaxed = true)
        taskRepositoryImpl = TaskRepositoryImpl(mockRemoteDataSource)
    }

    @Test
    fun `should delegate createTask to local data source and return result`() = runTest {
        coEvery { mockRemoteDataSource.createTask(testTask) } returns testTask

        val result = taskRepositoryImpl.createTask(testTask)

        coVerify(exactly = 1) { mockRemoteDataSource.createTask(testTask) }
        assertThat(result).isEqualTo(testTask)
    }

    @Test
    fun `should delegate updateTask to local data source and return updated task`() = runTest {
        coEvery { mockRemoteDataSource.updateTask(updatedTask) } returns updatedTask

        val result = taskRepositoryImpl.updateTask(updatedTask)

        coVerify(exactly = 1) { mockRemoteDataSource.updateTask(updatedTask) }
        assertThat(result).isEqualTo(updatedTask)
    }

    @Test
    fun `should delegate deleteTask to local data source`() = runTest {
        taskRepositoryImpl.deleteTask(testTask.id)

        coVerify(exactly = 1) { mockRemoteDataSource.deleteTask(testTask.id) }
    }

    @Test
    fun `should delegate getAllTasks to local data source and return task list`() = runTest {
        coEvery { mockRemoteDataSource.getAllTasks() } returns taskList

        val result = taskRepositoryImpl.getAllTasks()

        coVerify(exactly = 1) { mockRemoteDataSource.getAllTasks() }
        assertThat(result).isEqualTo(taskList)
        assertThat(result).hasSize(2)
    }

    @Test
    fun `should delegate getTaskById to local data source and return task when found`() = runTest {
        coEvery { mockRemoteDataSource.getTaskById(testTask.id) } returns testTask

        val result = taskRepositoryImpl.getTaskById(testTask.id)

        coVerify(exactly = 1) { mockRemoteDataSource.getTaskById(testTask.id) }
        assertThat(result).isEqualTo(testTask)
    }

    @Test
    fun `should return null when getTaskById is called for non-existent task`() = runTest {
        val nonExistentId = "non-existent-id"
        coEvery { mockRemoteDataSource.getTaskById(nonExistentId) } returns null

        val result = taskRepositoryImpl.getTaskById(nonExistentId)

        coVerify(exactly = 1) { mockRemoteDataSource.getTaskById(nonExistentId) }
        assertThat(result).isNull()
    }
}