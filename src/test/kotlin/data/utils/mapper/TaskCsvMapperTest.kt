package data.utils.mapper

import com.google.common.truth.Truth.assertThat
import org.example.data.utils.mapper.CsvLine
import org.example.data.utils.mapper.toCsvLines
import org.example.data.utils.mapper.toTasks
import org.example.logic.models.Task
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class TaskCsvMapperTest {
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
                "id,name,stateId,addedBy,auditLogsIds,projectId",
                "task-1,First Task,active,user-1,audit-1|audit-2,project-1",
                "task-2,Second Task,pending,user-2,,project-2"
            )

            val tasks = csvLines.toTasks()

            assertThat(tasks).hasSize(2)

            val expectedTask1 = Task(
                id = "task-1",
                name = "First Task",
                stateId = "active",
                addedBy = "user-1",
                auditLogsIds = listOf("audit-1", "audit-2"),
                projectId = "project-1"
            )

            val expectedTask2 = Task(
                id = "task-2",
                name = "Second Task",
                stateId = "pending",
                addedBy = "user-2",
                auditLogsIds = listOf(),
                projectId = "project-2"
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
            assertThat(csvLines[0]).isEqualTo("id,name,stateId,addedBy,auditLogsIds,projectId")
        }

        @Test
        fun `should convert tasks to CSV lines when they are correct and available`() {
            val tasks = listOf(
                Task(
                    id = "task-1",
                    name = "First Task",
                    stateId = "active",
                    addedBy = "user-1",
                    auditLogsIds = listOf("audit-1", "audit-2"),
                    projectId = "project-1"
                ), Task(
                    id = "task-2",
                    name = "Second Task",
                    stateId = "pending",
                    addedBy = "user-2",
                    auditLogsIds = emptyList(),
                    projectId = "project-2"
                )
            )

            val csvLines = tasks.toCsvLines()

            assertThat(csvLines).hasSize(3)
            assertThat(csvLines[0]).isEqualTo("id,name,stateId,addedBy,auditLogsIds,projectId")
            assertThat(csvLines[1]).isEqualTo("task-1,First Task,active,user-1,audit-1|audit-2,project-1")
            assertThat(csvLines[2]).isEqualTo("task-2,Second Task,pending,user-2,,project-2")
        }

        @Test
        fun `should throw IllegalArgumentException when task name contains comma`() {
            val tasks = listOf(
                Task(
                    id = "task-1",
                    name = "Task, with comma",
                    stateId = "active",
                    addedBy = "user-1",
                    auditLogsIds = listOf("audit-1"),
                    projectId = "project-1"
                )
            )

            assertThrows<IllegalArgumentException> {
                val csvLines = tasks.toCsvLines()
            }
        }
    }
}