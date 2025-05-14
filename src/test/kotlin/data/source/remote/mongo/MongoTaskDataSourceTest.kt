package data.source.remote.mongo

import com.mongodb.MongoTimeoutException
import com.mongodb.client.model.Filters
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import com.mongodb.kotlin.client.coroutine.FindFlow
import com.mongodb.kotlin.client.coroutine.MongoCollection
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import mockdata.createTask
import org.example.data.source.remote.models.TaskDTO
import org.example.data.source.remote.mongo.MongoTaskDataSource
import org.example.data.source.remote.mongo.utils.mapper.toTaskDTO
import org.example.data.utils.Constants.ID
import org.example.data.utils.Constants.STATE_ID_FIELD
import org.example.logic.models.Task
import org.example.logic.utils.TaskCreationFailedException
import org.example.logic.utils.TaskDeletionFailedException
import org.example.logic.utils.TaskNotChangedException
import org.example.logic.utils.TaskNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MongoTaskDataSourceTest {
    private lateinit var mongoTaskDataSource: MongoTaskDataSource
    private lateinit var mongoClient: MongoCollection<TaskDTO>
    private lateinit var testTasks: List<Task>
    private lateinit var testTaskDTOs: List<TaskDTO>
    private val ids = List(6) { Uuid.random() }
    @BeforeEach
    fun setUp() {
        mongoClient = mockk(relaxed = true)
        mongoTaskDataSource = MongoTaskDataSource(mongoClient)
        testTasks = listOf(
            createTask(ids[0], "Updated Task", ids[3], "in-progress", ids[5]),
            createTask(ids[1], "remove Task", ids[4], "todo", ids[5]),
        )
        testTaskDTOs = testTasks.map { it.toTaskDTO() }
    }

    @Nested
    inner class CreateTaskTests {
        @Test
        fun `createTask should return task when task is created`() = runTest {
            val insertOneResult = mockk<InsertOneResult>(relaxed = true)
            coEvery { mongoClient.insertOne(testTaskDTOs[0], any()) } returns insertOneResult

            val result = mongoTaskDataSource.createTask(testTasks[0])

            assertEquals(testTasks[0], result)
            coVerify { mongoClient.insertOne(testTaskDTOs[0], any()) }

        }

        @Test
        fun `createTask should throw TaskCreationFailedException when creation task failed MongoDB`() = runTest {

            coEvery { mongoClient.insertOne(testTaskDTOs[0], any()) } throws TaskCreationFailedException()

            assertThrows<TaskCreationFailedException> { mongoTaskDataSource.createTask(task = testTasks[0]) }
        }

    }

    @Nested
    inner class UpdateTaskTests {
        @Test
        fun `updateTask should return task when task is updated `() = runTest {

            val replaceResult = mockk<UpdateResult>(relaxed = true)
            coEvery {
                mongoClient.replaceOne(Filters.eq(ID, testTasks[0].id.toHexString()), testTaskDTOs[0], any())
            } returns replaceResult

            val result = mongoTaskDataSource.updateTask(testTasks[0])

            assertEquals(testTasks[0], result)
            coVerify { mongoClient.replaceOne(Filters.eq(ID, testTasks[0].id.toHexString()), testTaskDTOs[0], any()) }
        }

        @Test
        fun `updateTask should throw TaskNotChangedException  when update task fails`() = runTest {
            coEvery {
                mongoClient.replaceOne(Filters.eq(ID, testTasks[0].id.toHexString()), testTaskDTOs[0], any())
            } throws TaskNotChangedException()

            assertThrows<TaskNotChangedException> { mongoTaskDataSource.updateTask(testTasks[0]) }
        }

    }

    @Nested
    inner class GetTaskTests {
        @Test
        fun `getAllTasks should return list of all available tasks `() = runTest {
            val findFlow = mockk<FindFlow<List<TaskDTO>>>(relaxed = true)
            coEvery { mongoClient.find<List<TaskDTO>>(filter = any(), any()) } returns findFlow
            coEvery { findFlow.firstOrNull() } returns testTaskDTOs

            mongoTaskDataSource.getTaskById(ids[1])


            coVerify(exactly = 1) { mongoClient.find(filter = any()) }
        }

        @Test
        fun `getTaskById should return task when task found`() = runTest {
            val findFlow = mockk<FindFlow<TaskDTO>>(relaxed = true)
            every { mongoClient.find<TaskDTO>(filter = any()) } returns findFlow
            coEvery { findFlow.firstOrNull() } returns testTaskDTOs[0]

            mongoTaskDataSource.getTaskById(ids[2])

            coVerify(exactly = 1) { mongoClient.find(filter = any()) }

        }

        @Test
        fun `getTaskById should return null when  task not found`() = runTest {

            coEvery { mongoClient.find(Filters.eq(ID, "10")).firstOrNull() } returns null

            val result = mongoTaskDataSource.getTaskById(ids[1])

            assertNull(result)
            coVerify(exactly = 1) { mongoClient.find(filter = any()) }
        }

        @Test
        fun `getAllTasks should throw TaskNotFoundException when get all tasks fails`() = runTest {

            every {
                mongoClient.find(filter = any())
            } throws TaskNotFoundException()

            assertThrows<TaskNotFoundException> { mongoTaskDataSource.getAllTasks() }
        }

        @Test
        fun `geTaskById should throw TaskNotFoundException when get task by Id fails`() = runTest {

            every {
                mongoClient.find(filter = any())
            } throws TaskNotFoundException()

            assertThrows<TaskNotFoundException> { mongoTaskDataSource.getTaskById(ids[1]) }
        }

    }

    @Nested
    inner class DeleteTaskTests {

        @Test
        fun `deleteTask should delete task when it exist`() = runTest {
            val taskId = ids[2]
            val deleteResult = mockk<DeleteResult>(relaxed = true)

            coEvery { mongoClient.deleteOne(Filters.eq(ID, taskId.toHexString()), any()) } returns deleteResult

            mongoTaskDataSource.deleteTask(taskId)

            coVerify { mongoClient.deleteOne(Filters.eq(ID, taskId.toHexString()), any()) }
        }

        @Test
        fun `deleteTask should throw DeleteItemFailedException when deleting task by ID fails`() =
            runTest {

                coEvery { mongoClient.deleteOne(filter = any(), options = any()) } throws TaskDeletionFailedException()

                assertThrows<TaskDeletionFailedException> { mongoTaskDataSource.deleteTask(ids[1]) }
            }
    }

    @Nested
    inner class GetTasksByProjectStateTests{
        @Test
        fun `getTaskByProjectState should delete when task exist`() = runTest {
            val stateId = ids[4]
            val findFlow = mockk<FindFlow<TaskDTO>>(relaxed = true)
            coEvery {
                mongoClient.find(any(), any())
            } returns findFlow

            mongoTaskDataSource.getTasksByProjectState(stateId)

            coVerify {
                mongoClient.find(
                    Filters.eq(STATE_ID_FIELD, stateId.toHexString())
                )
            }
        }

        @Test
        fun `getTaskByProjectState should throw MongoTimeoutException when when happen incorrect configuration`() =
            runTest {
                val stateId = ids[1]
                coEvery { mongoClient.find(filter = any()) } throws MongoTimeoutException("Error")

                assertThrows<MongoTimeoutException> { mongoTaskDataSource.getTasksByProjectState(stateId) }
            }

    }

}