package data.source.local

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.example.data.source.local.csv.CsvTaskDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.logic.models.Task
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
    val id1 = Uuid.random()
    val id2 = Uuid.random()
    val id3 = Uuid.random()

    private val headerLine = "id,name,stateId,addedBy,auditLogsIds,projectId"
    private val taskCsvLine = "$id1,Initial Task,$id1,$id1,$id1|$id3,$id1"

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
            val newTask = Task(id2, "New Task", id2, "stateName", id2, "name", id2)
            val newTaskCsvLine = "$id2,New Task,$id2,$id2,$id2,$id2"
            val result = csvTaskDataSource.createTask(newTask)

            assertThat(result).isEqualTo(newTask)
            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(
                    listOf(
                        headerLine,
                        taskCsvLine,
                        newTaskCsvLine,
                    ),
                )
            }
        }
    }

    @Nested
    inner class UpdateTaskTests {
        @Test
        fun `should update task when it exists`() {
            val updated = Task(id2, "New Task", id2, "stateName", id2, "name", id2)
            val updatedCsvLine = "1,Updated Task,done,user1,,proj1"
            every { csvReader.readLines() } returns listOf(headerLine, updatedCsvLine)

            val result = csvTaskDataSource.updateTask(updated)

            assertThat(result).isEqualTo(updated)
            assertThat(csvTaskDataSource.getTaskById(id1)).isEqualTo(updated)
            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(
                    listOf(
                        headerLine,
                        updatedCsvLine,
                    ),
                )
            }
        }

        @Test
        fun `should do nothing when task is not created before`() {
            val updated = Task(id2, "New Task", id2, "stateName", id2, "name", id2)

            csvTaskDataSource.updateTask(updated)

            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(
                    listOf(headerLine, taskCsvLine),
                )
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

            csvTaskDataSource.deleteTask(id1)

            assertThat(csvTaskDataSource.getAllTasks()).isEmpty()
        }

        @Test
        fun `should do nothing task when task with id does not exist`() {
            csvTaskDataSource.deleteTask(id2)

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
            assertThat(tasks[0].id).isEqualTo("1")
        }
    }

    @Nested
    inner class GetTaskByIDTests {
        @Test
        fun `should return task by ID when it is available`() {
            val task = csvTaskDataSource.getTaskById(id1)

            assertThat(task).isNotNull()
            assertThat(task?.name).isEqualTo("Initial Task")
        }

        @Test
        fun `should return null when task does not exist`() {
            val task = csvTaskDataSource.getTaskById(id2)

            assertThat(task).isNull()
        }
    }
}
