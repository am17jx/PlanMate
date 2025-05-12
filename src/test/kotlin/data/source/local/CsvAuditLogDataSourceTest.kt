package data.source.local

import com.google.common.truth.Truth
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.datetime.Clock
import mockdata.createAuditLog
import org.example.data.source.local.csv.CsvAuditLogDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.data.source.local.csv.utils.mapper.toCsvRow
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLog.ActionType
import org.example.logic.models.AuditLog.EntityType
import org.example.logic.utils.toUuid
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


    private val audiLogID = Uuid.random()
    private val audiLogID2 = Uuid.random()
    private val currentTime = Clock.System.now()
    private val entityId = Uuid.random()
    private val entityId2 = Uuid.random()
    private val userId = Uuid.random()

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
                    id = audiLogID,
                    userId = userId,
                    createdAt = currentTime,
                    entityType = AuditLog.EntityType.TASK,
                    entityId = entityId,
                    actionType = AuditLog.ActionType.UPDATE,
                )
            val result = csvAuditLogDataSource.saveAuditLog(newLog)

            Truth.assertThat(result).isEqualTo(newLog)
            verify { csvReader.readLines() }
            verify { csvWriter.writeLines(any()) }
        }
    }

    @Nested
    inner class GetEntityLogsTests {
        @Test
        fun `should return logs  when it is available`() {

            val newLogCsvRow =
                "${audiLogID.toHexString()},${userId.toHexString()},,${currentTime.toEpochMilliseconds()},TASK,${entityId.toHexString()},,UPDATE,,,"
            val newLog2CsvRow =
                "${audiLogID2.toHexString()},${userId.toHexString()},,${currentTime.toEpochMilliseconds()},TASK,${entityId2.toHexString()},,UPDATE,,,"
            every { csvReader.readLines() } returns listOf(newLog2CsvRow, newLogCsvRow)

            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)

            val log = csvAuditLogDataSource.getEntityLogs(entityId, AuditLog.EntityType.TASK)

            Truth.assertThat(log).isNotNull()
            Truth.assertThat(log.size).isEqualTo(1)
        }

        @Test
        fun `should return null when entity not found `() {
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogs(entityId, AuditLog.EntityType.TASK)
            Truth.assertThat(log).isEmpty()
        }
    }

    @Nested
    inner class GetEntityLogsByIdTests {
        @Test
        fun `should return logs by ID when they are available`() {
            val newLogCsvRow =
                "${audiLogID.toHexString()},${userId.toHexString()},,${currentTime.toEpochMilliseconds()},TASK,${entityId.toHexString()},,UPDATE,,,"
            val newLog2CsvRow =
                "${audiLogID2.toHexString()},${userId.toHexString()},,${currentTime.toEpochMilliseconds()},TASK,${entityId2.toHexString()},,UPDATE,,,"
            every { csvReader.readLines() } returns listOf(newLog2CsvRow, newLogCsvRow)
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogByLogId(audiLogID)

            Truth.assertThat(log).isNotNull()
            Truth.assertThat(log?.id).isEqualTo(audiLogID)
        }

        @Test
        fun `should return null when entity not found `() {
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)
            val log = csvAuditLogDataSource.getEntityLogByLogId(audiLogID)
            Truth.assertThat(log).isNull()
        }
    }

    @Nested
    inner class DeleteAuditLogTests {
        @Test
        fun `should do nothing when entity not found`() {
            val existingLogCsv =
                "${audiLogID.toHexString()},${userId.toHexString()},,${currentTime.toEpochMilliseconds()},TASK,${entityId.toHexString()},,UPDATE,,,"
            val auditLogToDelete =
                createAuditLog(
                    id = audiLogID,
                    userId = userId,
                    entityType = AuditLog.EntityType.TASK,
                    entityId = entityId,
                    actionType = AuditLog.ActionType.CREATE,
                )

            every { csvReader.readLines() } returns listOf(existingLogCsv)
            csvAuditLogDataSource = CsvAuditLogDataSource(csvReader, csvWriter)

            csvAuditLogDataSource.deleteAuditLog(auditLogToDelete.id)

            verify { csvWriter.writeLines(any()) }
        }

    }
}
