package data.source.local

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.example.data.source.local.csv.CsvTaskDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.logic.models.Task
import org.example.logic.utils.toUuid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CsvTaskDataSourceTest {
    private lateinit var csvReader: CSVReader
    private lateinit var csvWriter: CSVWriter
    private lateinit var csvTaskDataSource: CsvTaskDataSource
    val id = Uuid.random()
    private val taskId = Uuid.random().toHexString()
    private val stateId = Uuid.random().toHexString()
    private val addedById = Uuid.random().toHexString()
    private val projectId = Uuid.random().toHexString()

    private val headerLine = "id,name,stateId,stateName,addedById,addedByName,projectId"
    private val taskCsvLine = "$taskId,Initial Task,$stateId,stateName,$addedById,name,$projectId"

    @BeforeEach
    fun setUp() {
        csvReader = mockk(relaxed = true)
        every { csvReader.readLines() } returns listOf(headerLine, taskCsvLine)
        csvWriter = mockk(relaxed = true)
        csvTaskDataSource = CsvTaskDataSource(csvReader, csvWriter)
    }

    @Nested
    inner class CreateTaskTests {
        @Test
        fun `should add a new task when function is called`() {
            val newTask = Task(id, "New Task", id, "stateName", id, "name", id)
            val result = csvTaskDataSource.createTask(newTask)

            assertThat(result).isEqualTo(newTask)
            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(any())
            }
        }
    }

    @Nested
    inner class UpdateTaskTests {
        @Test
        fun `should update task when it exists`() {
            val updated = Task(taskId.toUuid(), "New Task", stateId.toUuid(), "stateName", addedById.toUuid(), "name", projectId.toUuid())
            val updatedCsvLine = "$taskId,New Task,$stateId,stateName,$addedById,name,$projectId"
            every { csvReader.readLines() } returns listOf(headerLine, updatedCsvLine)

            val result = csvTaskDataSource.updateTask(updated)

            assertThat(result).isEqualTo(updated)
            assertThat(csvTaskDataSource.getTaskById(taskId.toUuid())).isEqualTo(updated)
            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(any())
            }
        }

        @Test
        fun `should do nothing when task is not created before`() {
            val updated = Task(id, "New Task", id, "stateName", id, "name", id)

            csvTaskDataSource.updateTask(updated)

            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(any())
            }
        }
    }

    @Nested
    inner class DeleteTaskTests {
        @Test
        fun `should delete task when it exists`() {
            var lines = listOf(headerLine, taskCsvLine)
            every { csvReader.readLines() } answers { lines }
            every { csvWriter.writeLines(any(), any()) } answers {
                val writtenLines = firstArg<List<String>>()
                if ("1" !in writtenLines) {
                    lines = listOf(headerLine)
                }
            }

            csvTaskDataSource.deleteTask(taskId.toUuid())

            assertThat(csvTaskDataSource.getAllTasks()).isEmpty()
        }

        @Test
        fun `should do nothing task when task with id does not exist`() {
            csvTaskDataSource.deleteTask(id)

            assertThat(csvTaskDataSource.getAllTasks()).isNotEmpty()
        }
    }

    @Nested
    inner class GetAllTasksTests {
        @Test
        fun `should return all available tasks`() {
            val tasks = csvTaskDataSource.getAllTasks()

            verify(exactly = 1) { csvReader.readLines() }
            assertThat(tasks).hasSize(1)
            assertThat(tasks[0].id).isEqualTo(taskId.toUuid())
        }
    }

    @Nested
    inner class GetTaskByIDTests {
        @Test
        fun `should return task by ID when it is available`() {
            val task = csvTaskDataSource.getTaskById(taskId.toUuid())

            assertThat(task).isNotNull()
            assertThat(task?.name).isEqualTo("Initial Task")
        }

        @Test
        fun `should return null when task does not exist`() {
            val task = csvTaskDataSource.getTaskById(id)

            assertThat(task).isNull()
        }
    }

    @Nested
    inner class GetTasksByProjectState {
        @Test
        fun `should return task by state ID when it is available`() = runTest {
            val task = csvTaskDataSource.getTasksByProjectState(stateId.toUuid())

            assertThat(task).isNotNull()
            assertThat(task[0].name).isEqualTo("Initial Task")
        }
    }
}
