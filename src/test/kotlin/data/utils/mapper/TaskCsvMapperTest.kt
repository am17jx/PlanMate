package data.utils.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.source.local.csv.utils.mapper.CsvLine
import org.example.data.source.local.csv.utils.mapper.toCsvLines
import org.example.data.source.local.csv.utils.mapper.toTasks
import org.example.logic.models.Task
import org.example.logic.utils.toUuid
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class TaskCsvMapperTest {

    val testId = Uuid.random()

    @Nested
    inner class ToTasksTests {
        @Test
        fun `should return empty list when input is empty`() {
            val csvLines = emptyList<CsvLine>()

            val tasks = csvLines.toTasks()

            assertThat(tasks).isEmpty()
        }

        @Test
        fun `should return empty list when input contains only headers`() {
            val csvLines = listOf("id,name,stateId,addedBy,auditLogsIds,projectId")

            val tasks = csvLines.toTasks()

            assertThat(tasks).isEmpty()
        }

        @Test
        fun `should correctly parse CSV lines when they are valid and available`() {
            val csvLines = listOf(
                "id,name,stateId,stateName,addedById,addedByName,projectId",
                "${testId.toHexString()},First Task,${testId.toHexString()},stateName,${testId.toHexString()},user-1,${testId.toHexString()}",
                "${testId.toHexString()},Second Task,${testId.toHexString()},stateName,${testId.toHexString()},user-2,${testId.toHexString()}"
            )

            val tasks = csvLines.toTasks()

            assertThat(tasks).hasSize(2)

            val expectedTask1 = Task(
                id = testId,
                name = "First Task",
                stateId = testId,
                stateName = "stateName",
                addedById = testId,
                addedByName = "user-1",
                projectId = testId
            )

            val expectedTask2 = Task(
                id = testId,
                name = "Second Task",
                stateId = testId,
                stateName = "stateName",
                addedById = testId,
                addedByName = "user-2",
                projectId = testId
            )
            assertThat(expectedTask1).isEqualTo(tasks[0])
            assertThat(expectedTask2).isEqualTo(tasks[1])
        }

        @Test
        fun `should throw IllegalArgumentException when CSV line has insufficient fields`() {
            val csvLines = listOf(
                "id,name,stateId,addedBy,auditLogsIds,projectId", "task-1,Task Name"
            )

            assertThrows<IllegalArgumentException> {
                csvLines.toTasks()
            }
        }
    }

    @Nested
    inner class ToCsvLinesTests {
        @Test
        fun `should return header when task list is empty`() {
            val tasks = emptyList<Task>()
            val csvLines = tasks.toCsvLines()

            assertThat(csvLines).hasSize(1)
            assertThat(csvLines[0]).isEqualTo("id,name,stateId, stateName, addedById, addedByName, projectId")
        }

        @Test
        fun `should convert tasks to CSV lines when they are correct and available`() {
            val tasks = listOf(
                Task(
                    id = testId,
                    name = "First Task",
                    stateId = testId,
                    stateName = "stateName",
                    addedById = testId,
                    addedByName = "user-1",
                    projectId = testId
                ), Task(
                    id = testId,
                    name = "Second Task",
                    stateId = testId,
                    stateName = "stateName",
                    addedById = testId,
                    addedByName = "user-2",
                    projectId = testId
                )
            )

            val csvLines = tasks.toCsvLines()

            assertThat(csvLines).hasSize(3)
            assertThat(csvLines[0]).isEqualTo("id,name,stateId, stateName, addedById, addedByName, projectId")
            assertThat(csvLines[1]).isEqualTo("$testId,First Task,$testId,stateName,${testId.toHexString()},user-1,$testId")
            assertThat(csvLines[2]).isEqualTo("$testId,Second Task,$testId,stateName,${testId.toHexString()},user-2,$testId")
        }

        @Test
        fun `should throw IllegalArgumentException when task name contains comma`() {
            val tasks = listOf(
                Task(
                    id = Uuid.random(),
                    name = "First, Task",
                    stateId = Uuid.random(),
                    stateName = "stateName",
                    addedById = Uuid.random(),
                    addedByName = "user-1",
                    projectId = Uuid.random()
                )
            )

            assertThrows<IllegalArgumentException> {
                tasks.toCsvLines()
            }
        }
    }
}