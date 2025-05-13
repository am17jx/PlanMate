package data.repository

import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import mockdata.createTask
import org.example.data.repository.TaskRepositoryImpl
import org.example.data.repository.sources.remote.RemoteTaskDataSource
import org.example.logic.utils.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TaskRepositoryImplTest {
    private lateinit var mockRemoteDataSource: RemoteTaskDataSource
    private lateinit var taskRepositoryImpl: TaskRepositoryImpl
    private val id1 = Uuid.random()
    private val id2 = Uuid.random()
    private val id3 = Uuid.random()

    private val testTask = createTask(
        id = id1,
        name = "Test Task",
    )

    private val updatedTask = testTask.copy(
        name = "Updated Task",
        stateId = id2,
    )

    private val taskList = listOf(
        testTask,
        createTask(id = id2, name = "Task 2"),
    )

    @BeforeEach
    fun setUp() {
        mockRemoteDataSource = mockk(relaxed = true)
        taskRepositoryImpl = TaskRepositoryImpl(mockRemoteDataSource)
    }

    @Test
    fun `should return created task when createTask succeeds`() = runTest {
        coEvery { mockRemoteDataSource.createTask(testTask) } returns testTask

        val result = taskRepositoryImpl.createTask(testTask)

        coVerify(exactly = 1) { mockRemoteDataSource.createTask(testTask) }
        assertThat(result).isEqualTo(testTask)
    }

    @Test
    fun `should throw TaskCreationFailedException when createTask fails`() = runTest {
        coEvery { mockRemoteDataSource.createTask(testTask) } throws RuntimeException("error")

        val exception = runCatching {
            taskRepositoryImpl.createTask(testTask)
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(TaskCreationFailedException::class.java)
    }

    @Test
    fun `should return updated task when updateTask succeeds`() = runTest {
        coEvery { mockRemoteDataSource.updateTask(updatedTask) } returns updatedTask

        val result = taskRepositoryImpl.updateTask(updatedTask)

        coVerify(exactly = 1) { mockRemoteDataSource.updateTask(updatedTask) }
        assertThat(result).isEqualTo(updatedTask)
    }

    @Test
    fun `should throw TaskNotChangedException when updateTask fails`() = runTest {
        coEvery { mockRemoteDataSource.updateTask(updatedTask) } throws RuntimeException("error")

        val exception = runCatching {
            taskRepositoryImpl.updateTask(updatedTask)
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(TaskNotChangedException::class.java)
    }

    @Test
    fun `should call remote data source once when deleteTask is called`() = runTest {
        taskRepositoryImpl.deleteTask(testTask.id)

        coVerify(exactly = 1) { mockRemoteDataSource.deleteTask(testTask.id) }
    }

    @Test
    fun `should throw TaskDeletionFailedException when deleteTask fails`() = runTest {
        coEvery { mockRemoteDataSource.deleteTask(testTask.id) } throws RuntimeException("error")

        val exception = runCatching {
            taskRepositoryImpl.deleteTask(testTask.id)
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(TaskDeletionFailedException::class.java)
    }

    @Test
    fun `should return all tasks when getAllTasks succeeds`() = runTest {
        coEvery { mockRemoteDataSource.getAllTasks() } returns taskList

        val result = taskRepositoryImpl.getAllTasks()

        coVerify(exactly = 1) { mockRemoteDataSource.getAllTasks() }
        assertThat(result).isEqualTo(taskList)
        assertThat(result).hasSize(2)
    }

    @Test
    fun `should throw NoTasksFoundException when getAllTasks fails`() = runTest {
        coEvery { mockRemoteDataSource.getAllTasks() } throws RuntimeException("error")

        val exception = runCatching {
            taskRepositoryImpl.getAllTasks()
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(NoTasksFoundException::class.java)
    }

    @Test
    fun `should return task when getTaskById finds task`() = runTest {
        coEvery { mockRemoteDataSource.getTaskById(testTask.id) } returns testTask

        val result = taskRepositoryImpl.getTaskById(testTask.id)

        coVerify(exactly = 1) { mockRemoteDataSource.getTaskById(testTask.id) }
        assertThat(result).isEqualTo(testTask)
    }

    @Test
    fun `should return null when getTaskById does not find task`() = runTest {
        val nonExistentId = id3
        coEvery { mockRemoteDataSource.getTaskById(nonExistentId) } returns null

        val result = taskRepositoryImpl.getTaskById(nonExistentId)

        coVerify(exactly = 1) { mockRemoteDataSource.getTaskById(nonExistentId) }
        assertThat(result).isNull()
    }

    @Test
    fun `should throw NoTaskFoundException when getTaskById fails`() = runTest {
        coEvery { mockRemoteDataSource.getTaskById(testTask.id) } throws RuntimeException("error")

        val exception = runCatching {
            taskRepositoryImpl.getTaskById(testTask.id)
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(NoTaskFoundException::class.java)
    }

    @Test
    fun `should return tasks by project state when getTasksByProjectState succeeds`() = runTest {
        coEvery { mockRemoteDataSource.getTasksByProjectState(id1) } returns taskList

        val result = taskRepositoryImpl.getTasksByProjectState(id1)

        coVerify(exactly = 1) { mockRemoteDataSource.getTasksByProjectState(id1) }
        assertThat(result).isEqualTo(taskList)
    }

    @Test
    fun `should throw NoTaskFoundException when getTasksByProjectState fails`() = runTest {
        coEvery { mockRemoteDataSource.getTasksByProjectState(id1) } throws RuntimeException("error")

        val exception = runCatching {
            taskRepositoryImpl.getTasksByProjectState(id1)
        }.exceptionOrNull()

        assertThat(exception).isInstanceOf(NoTaskFoundException::class.java)
    }
}