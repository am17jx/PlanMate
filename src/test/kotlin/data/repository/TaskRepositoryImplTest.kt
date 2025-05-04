package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mockdata.createTask
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.source.local.contract.LocalTaskDataSource
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TaskRepositoryImplTest {
    private lateinit var mockLocalTaskDataSource: LocalTaskDataSource
    private lateinit var taskRepositoryImpl: TaskRepositoryImpl

    // Test data
    private val testTask = createTask(
        id = "task-1",
        name = "Test Task",
    )

    private val updatedTask = testTask.copy(
        name = "Updated Task",
        stateId = "state-2"
    )

    private val taskList = listOf(
        testTask,
        createTask(
            id = "task-2",
            name = "Task 2",
        )
    )

    @BeforeEach
    fun setUp() {
        mockLocalTaskDataSource = mockk(relaxed = true)
        taskRepositoryImpl = TaskRepositoryImpl(mockLocalTaskDataSource)
    }

    @Test
    fun `should delegate createTask to local data source and return result`() {
        every { mockLocalTaskDataSource.createTask(testTask) } returns testTask

        val result = taskRepositoryImpl.createTask(testTask)

        verify(exactly = 1) { mockLocalTaskDataSource.createTask(testTask) }
        assertThat(result).isEqualTo(testTask)
    }

    @Test
    fun `should delegate updateTask to local data source and return updated task`() {
        every { mockLocalTaskDataSource.updateTask(updatedTask) } returns updatedTask

        val result = taskRepositoryImpl.updateTask(updatedTask)

        verify(exactly = 1) { mockLocalTaskDataSource.updateTask(updatedTask) }
        assertThat(result).isEqualTo(updatedTask)
    }

    @Test
    fun `should delegate deleteTask to local data source`() {
        taskRepositoryImpl.deleteTask(testTask.id)

        verify(exactly = 1) { mockLocalTaskDataSource.deleteTask(testTask.id) }
    }

    @Test
    fun `should delegate getAllTasks to local data source and return task list`() {
        every { mockLocalTaskDataSource.getAllTasks() } returns taskList

        val result = taskRepositoryImpl.getAllTasks()

        verify(exactly = 1) { mockLocalTaskDataSource.getAllTasks() }
        assertThat(result).isEqualTo(taskList)
        assertThat(result).hasSize(2)
    }

    @Test
    fun `should delegate getTaskById to local data source and return task when found`() {
        every { mockLocalTaskDataSource.getTaskById(testTask.id) } returns testTask

        val result = taskRepositoryImpl.getTaskById(testTask.id)

        verify(exactly = 1) { mockLocalTaskDataSource.getTaskById(testTask.id) }
        assertThat(result).isEqualTo(testTask)
    }

    @Test
    fun `should return null when getTaskById is called for non-existent task`() {
        val nonExistentId = "non-existent-id"
        every { mockLocalTaskDataSource.getTaskById(nonExistentId) } returns null

        val result = taskRepositoryImpl.getTaskById(nonExistentId)

        verify(exactly = 1) { mockLocalTaskDataSource.getTaskById(nonExistentId) }
        assertThat(result).isNull()
    }
}