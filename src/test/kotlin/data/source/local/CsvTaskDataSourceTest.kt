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

class CsvTaskDataSourceTest {
    private lateinit var csvReader: CSVReader
    private lateinit var csvWriter: CSVWriter
    private lateinit var csvTaskDataSource: CsvTaskDataSource
    private val headerLine = "id,name,stateId,addedBy,auditLogsIds,projectId"
    private val taskCsvLine = "1,Initial Task,open,user1,audit1|audit2,proj1"

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
            val newTask = Task("2", "New Task", "todo", "user2", listOf("audit3"), "proj2")
            val newTaskCsvLine = "2,New Task,todo,user2,audit3,proj2"
            val result = csvTaskDataSource.createTask(newTask)

            assertThat(result).isEqualTo(newTask)
            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(
                    listOf(
                        headerLine,
                        taskCsvLine,
                        newTaskCsvLine
                    )
                )
            }
        }
    }

    @Nested
    inner class UpdateTaskTests {
        @Test
        fun `should update task when it exists`() {
            val updated = Task("1", "Updated Task", "done", "user1", emptyList(), "proj1")
            val updatedCsvLine = "1,Updated Task,done,user1,,proj1"
            every { csvReader.readLines() } returns listOf(headerLine, updatedCsvLine)

            val result = csvTaskDataSource.updateTask(updated)

            assertThat(result).isEqualTo(updated)
            assertThat(csvTaskDataSource.getTaskById("1")).isEqualTo(updated)
            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(
                    listOf(
                        headerLine,
                        updatedCsvLine
                    )
                )
            }
        }
        @Test
        fun `should do nothing when task is not created before`() {
            val updated = Task("2", "Updated Task", "done", "user1", emptyList(), "proj1")

            csvTaskDataSource.updateTask(updated)

            verify(exactly = 2) { csvReader.readLines() }
            verify(exactly = 1) {
                csvWriter.writeLines(
                    listOf(headerLine, taskCsvLine)
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

            csvTaskDataSource.deleteTask("1")

            assertThat(csvTaskDataSource.getAllTasks()).isEmpty()
        }

        @Test
        fun `should do nothing task when task with id does not exist`() {
            csvTaskDataSource.deleteTask("2")

            assertThat(csvTaskDataSource.getAllTasks()).isNotEmpty()
        }
    }

    @Nested
    inner class GetAllTasksTests {
        @Test
        fun `should return all available tasks`() {
            val tasks = csvTaskDataSource.getAllTasks()

            verify (exactly = 1){ csvReader.readLines() }
            assertThat(tasks).hasSize(1)
            assertThat(tasks[0].id).isEqualTo("1")
        }
    }

    @Nested
    inner class GetTaskByIDTests {
        @Test
        fun `should return task by ID when it is available`() {
            val task = csvTaskDataSource.getTaskById("1")

            assertThat(task).isNotNull()
            assertThat(task?.name).isEqualTo("Initial Task")
        }

        @Test
        fun `should return null when task does not exist`() {
            val task = csvTaskDataSource.getTaskById("2")

            assertThat(task).isNull()
        }
    }
}