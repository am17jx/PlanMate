package data.source

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import mockdata.createAuditLog
import org.example.data.source.CsvAuditLogDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.example.data.utils.mapper.toCsvRow
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test

class CsvAuditLogDataSourceTest {
    private lateinit var csvReader: CSVReader
    private lateinit var csvWriter: CSVWriter
    private lateinit var csvAuditLogDataSource: CsvAuditLogDataSource

    @BeforeEach
    fun setup() {
        csvReader = mockk(relaxed = true)
        csvWriter = mockk(relaxed = true)
    }

    @Nested
    inner class SaveAuditLogTests {
        @Test
        fun `should write new audit when called`() {

            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)

            val newLog = createAuditLog(
                id = "asd2-qwe2-asdw-wer1",
                userId = "1",
                action = "user abc changed task XYZ-001 from InProgress to InDevReview",
                entityType = AuditLogEntityType.TASK,
                entityId = "2",
                actionType = AuditLogActionType.UPDATE
            )
            val newLogCsvRow =
                "asd2-qwe2-asdw-wer1,1,user abc changed task XYZ-001 from InProgress to InDevReview,0,TASK,2,UPDATE"
            val result = csvAuditLogDataSource.saveAuditLog(newLog)

            assertThat(result).isEqualTo(newLog)
            verify() { csvReader.readLines() }
            verify() { csvWriter.writeLines(listOf(newLogCsvRow)) }
        }

    }

    @Nested
    inner class GetEntityLogsTests {
        @Test
        fun `should return logs  when it is available`() {
            val newLogCsvRow =
                "asd2-qwe2-asdw-wewe,1,user abc changed task XYZ-001 from InProgress to InDevReview,0,TASK,2,UPDATE"
            val newLog2CsvRow =
                "asd2-qwe2-asdw-wer1,1,user abc changed task XYZ-001 from InDevReview to InProgress,0,TASK,2,UPDATE"
            val newLog3CsvRow =
                "asd2-qwe2-sain-wer1,1,user abc changed task XYZ-001 from InDevReview to InProgress,0,PROJECT,2,UPDATE"
            every { csvReader.readLines() } returns listOf(newLog2CsvRow, newLogCsvRow, newLog3CsvRow)

            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)

            val log = csvAuditLogDataSource.getEntityLogs("2", AuditLogEntityType.TASK)

            assertThat(log).isNotNull()
            assertThat(log.size).isEqualTo(2)
        }

        @Test
        fun `should return null when entity not found `() {
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogs("2", AuditLogEntityType.TASK)
            assertThat(log).isEmpty()

        }
    }

    @Nested
    inner class GetEntityLogsByIdTests {
        @Test
        fun `should return logs by ID when they are available`() {
            val newLogCsvRow =
                "asd2-qwe2-asdw-wewe,1,user abc changed task XYZ-001 from InProgress to InDevReview,0,TASK,2,UPDATE"
            val newLog2CsvRow =
                "asd2-qwe2-asdw-wer1,1,user abc changed task XYZ-001 from InDevReview to InProgress,0,TASK,2,UPDATE"
            val newLog3CsvRow =
                "asd2-qwe2-sain-wer1,1,user abc changed task XYZ-001 from InDevReview to InProgress,0,PROJECT,2,UPDATE"
            every { csvReader.readLines() } returns listOf(newLog2CsvRow, newLogCsvRow, newLog3CsvRow)
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogByLogId("asd2-qwe2-asdw-wewe")

            assertThat(log).isNotNull()
            assertThat(log?.id).isEqualTo("asd2-qwe2-asdw-wewe")
        }

        @Test
        fun `should return null when entity not found `() {
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogByLogId("2")
            assertThat(log).isNull()

        }
    }

    @Nested
    inner class DeleteAuditLogTests {
        @Test
        fun `should do nothing when entity not found`() {
            val existingLogCsv = "log-id-1,1,action,1234567890,TASK,100,CREATE"
            val auditLogToDelete = createAuditLog(
                id = "non-existent-id",
                userId = "1",
                action = "some action",
                entityType = AuditLogEntityType.TASK,
                entityId = "100",
                actionType = AuditLogActionType.CREATE
            )

            every { csvReader.readLines() } returns listOf(existingLogCsv)
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)

            csvAuditLogDataSource.deleteAuditLog(auditLogToDelete.id)

            verify { csvWriter.writeLines(listOf(existingLogCsv)) }
        }

        @Test
        fun `should remove the log from file when the log found`() {
            val logToDelete = createAuditLog(
                id = "log-id-123",
                userId = "1",
                action = "user abc deleted task XYZ-001",
                timestamp = 1234567890,
                entityType = AuditLogEntityType.TASK,
                entityId = "2",
                actionType = AuditLogActionType.DELETE
            )
            val matchingCsvRow = logToDelete.toCsvRow()
            val otherLogCsvRow = createAuditLog(
                id = "log-id-999",
                userId = "2",
                action = "user def updated task XYZ-002",
                timestamp = 1234567891,
                entityType = AuditLogEntityType.TASK,
                entityId = "3",
                actionType = AuditLogActionType.UPDATE
            ).toCsvRow()

            every { csvReader.readLines() } returns listOf(matchingCsvRow, otherLogCsvRow)
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            csvAuditLogDataSource.deleteAuditLog(logToDelete.id)

            verify {
                csvWriter.writeLines(listOf(otherLogCsvRow))
            }
        }
    }
}