package data.utils.mapper

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Clock
import mockdata.createAuditLog
import org.example.data.source.local.csv.utils.mapper.toAuditLogs
import org.example.data.source.local.csv.utils.mapper.toCsvRow
import org.example.data.source.local.csv.utils.mapper.toCsvRows
import org.example.logic.models.AuditLog.ActionType
import org.example.logic.models.AuditLog.EntityType
import org.example.logic.utils.toInstant
import org.example.logic.utils.toUuid
import java.util.*
import kotlin.test.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class AuditLogCsvMapperKtTest {
    private val id1 = Uuid.random()

    @Test
    fun `toCsvRow should return a CSV row when an audit log given`() {
        val audiLogID = Uuid.random()
        val currentTime = Clock.System.now()
        val auditLog =
            createAuditLog(
                id = audiLogID,
                userId = id1,
                createdAt = currentTime,
                entityType = EntityType.TASK,
                entityId = id1,
                actionType = ActionType.UPDATE,
            )
        val expected =
            "${audiLogID.toHexString()},$id1,,${currentTime.toEpochMilliseconds()},TASK,$id1,,UPDATE,,,"

        val result = auditLog.toCsvRow()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toCsvRows should return a CSV rows when a list of audit log given`() {
        val audiLogID = Uuid.random()
        val audiLogID2 = Uuid.random()
        val currentTime = Clock.System.now()
        val auditLogs =
            listOf(
                createAuditLog(
                    id = audiLogID,
                    userId = id1,
                    createdAt = currentTime,
                    entityType = EntityType.TASK,
                    entityId = id1,
                    actionType = ActionType.UPDATE,
                ),
                createAuditLog(
                    id = audiLogID2,
                    userId = id1,
                    createdAt = currentTime,
                    entityType = EntityType.TASK,
                    entityId = id1,
                    actionType = ActionType.UPDATE,
                ),
            )
        val expected =
            listOf(
                "${audiLogID.toHexString()},$id1,,${currentTime.toEpochMilliseconds()},TASK,$id1,,UPDATE,,,",
                "${audiLogID2.toHexString()},$id1,,${currentTime.toEpochMilliseconds()},TASK,$id1,,UPDATE,,,",
            )
        val result = auditLogs.toCsvRows()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toAuditLogs should return list of audit log when list of CSV rows are given`() {
        val audiLogID = Uuid.random()
        val audiLogID2 = Uuid.random()
        val currentTime = Clock.System.now()
        val testId = "e009d0e87044482ba5ad6a60cd877a5e"
        val lisOfCsvRows =
            listOf(
                "${audiLogID.toHexString()},$testId,,${currentTime.toEpochMilliseconds()},TASK,$testId,,UPDATE,,,",
                "${audiLogID2.toHexString()},$testId,,${currentTime.toEpochMilliseconds()},TASK,$testId,,UPDATE,,,",
            )
        val expected =
            listOf(
                createAuditLog(
                    id = audiLogID,
                    userId = testId.toUuid(),
                    createdAt = currentTime.toEpochMilliseconds().toInstant(),
                    entityType = EntityType.TASK,
                    entityId = testId.toUuid(),
                    actionType = ActionType.UPDATE,
                ),
                createAuditLog(
                    id = audiLogID2,
                    userId = testId.toUuid(),
                    createdAt = currentTime.toEpochMilliseconds().toInstant(),
                    entityType = EntityType.TASK,
                    entityId = testId.toUuid(),
                    actionType = ActionType.UPDATE,
                ),
            )
        val result = lisOfCsvRows.toAuditLogs()

        assertThat(result).isEqualTo(expected)
    }
}
