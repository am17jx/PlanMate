package data.source

import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import mockdata.createAuditLog
import org.example.data.source.local.csv.CsvAuditLogDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.data.source.local.csv.utils.mapper.toCsvRow
import org.example.logic.models.AuditLog.ActionType
import org.example.logic.models.AuditLog.EntityType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CsvAuditLogDataSourceTest {
    private lateinit var csvReader: CSVReader
    private lateinit var csvWriter: CSVWriter
    private lateinit var csvAuditLogDataSource: CsvAuditLogDataSource
    private val id1 = Uuid.random()
    private val id2 = Uuid.random()
    private val id3 = Uuid.random()

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

            val currentTime = Clock.System.now()
            val newLog =
                createAuditLog(
                    id = id1,
                    userId = id2,
                    action = "user abc changed task XYZ-001 from InProgress to InDevReview",
                    createdAt = currentTime,
                    entityType = EntityType.TASK,
                    entityId = id3,
                    actionType = ActionType.UPDATE,
                )
            val newLogCsvRow =
                "${id1.toHexString()},${id2.toHexString()},user abc changed task XYZ-001 from InProgress to InDevReview,${currentTime.toEpochMilliseconds()},TASK,${id3.toHexString()},UPDATE"
            val result = csvAuditLogDataSource.saveAuditLog(newLog)

            assertThat(result).isEqualTo(newLog)
            verify { csvReader.readLines() }
            verify { csvWriter.writeLines(listOf(newLogCsvRow)) }
        }
    }

    @Nested
    inner class GetEntityLogsTests {
        @Test
        fun `should return logs  when it is available`() {
            val newLogCsvRow =
                "${id1.toHexString()},${id1.toHexString()},user abc changed task XYZ-001 from InProgress to InDevReview,123456789,TASK,${id1.toHexString()},UPDATE"
            val newLog2CsvRow =
                "${id2.toHexString()},${id2.toHexString()},user abc changed task XYZ-001 from InDevReview to InProgress,123456789,TASK,${id2.toHexString()},UPDATE"
            val newLog3CsvRow =
                "${id3.toHexString()},${id3.toHexString()},user abc changed task XYZ-001 from InDevReview to InProgress,123456789,PROJECT,${id3.toHexString()},UPDATE"
            every { csvReader.readLines() } returns listOf(newLog2CsvRow, newLogCsvRow, newLog3CsvRow)

            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)

            val log = csvAuditLogDataSource.getEntityLogs(id2, EntityType.TASK)

            assertThat(log).isNotNull()
            assertThat(log.size).isEqualTo(2)
        }

        @Test
        fun `should return null when entity not found `() {
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogs(id2, EntityType.TASK)
            assertThat(log).isEmpty()
        }
    }

    @Nested
    inner class GetEntityLogsByIdTests {
        @Test
        fun `should return logs by ID when they are available`() {
            val newLogCsvRow =
                "${id1.toHexString()},${id1.toHexString()},user abc changed task XYZ-001 from InProgress to InDevReview,123456789,TASK,${id1.toHexString()},UPDATE"
            val newLog2CsvRow =
                "${id2.toHexString()},${id2.toHexString()},user abc changed task XYZ-001 from InDevReview to InProgress,123456789,TASK,${id2.toHexString()},UPDATE"
            val newLog3CsvRow =
                "${id3.toHexString()},${id3.toHexString()},user abc changed task XYZ-001 from InDevReview to InProgress,123456789,PROJECT,${id3.toHexString()},UPDATE"
            every { csvReader.readLines() } returns listOf(newLog2CsvRow, newLogCsvRow, newLog3CsvRow)
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogByLogId(id1)

            assertThat(log).isNotNull()
            assertThat(log?.id).isEqualTo(id1)
        }

        @Test
        fun `should return null when entity not found `() {
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogByLogId(id2)
            assertThat(log).isNull()
        }
    }

    @Nested
    inner class DeleteAuditLogTests {
        @Test
        fun `should do nothing when entity not found`() {
            val existingLogCsv = "${id1.toHexString()},${id1.toHexString()},action,1234567890,TASK,${id1.toHexString()},CREATE"
            val auditLogToDelete =
                createAuditLog(
                    id = id2,
                    userId = id2,
                    action = "some action",
                    entityType = EntityType.TASK,
                    entityId = id3,
                    actionType = ActionType.CREATE,
                )

            every { csvReader.readLines() } returns listOf(existingLogCsv)
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)

            csvAuditLogDataSource.deleteAuditLog(auditLogToDelete.id)

            verify { csvWriter.writeLines(listOf(existingLogCsv)) }
        }

        @Test
        fun `should remove the log from file when the log found`() {
            val logToDelete =
                createAuditLog(
                    id = id1,
                    userId = id3,
                    action = "user abc deleted task XYZ-001",
                    createdAt = Clock.System.now(),
                    entityType = EntityType.TASK,
                    entityId = id2,
                    actionType = ActionType.DELETE,
                )
            val matchingCsvRow = logToDelete.toCsvRow()
            val otherLogCsvRow =
                createAuditLog(
                    id = id3,
                    userId = id2,
                    action = "user def updated task XYZ-002",
                    createdAt = Clock.System.now(),
                    entityType = EntityType.TASK,
                    entityId = id3,
                    actionType = ActionType.UPDATE,
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
