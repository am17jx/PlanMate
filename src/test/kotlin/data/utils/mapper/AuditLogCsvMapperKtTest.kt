package data.utils.mapper

import com.google.common.truth.Truth.assertThat
import kotlinx.datetime.Clock
import mockdata.createAuditLog
import org.example.data.source.local.csv.utils.mapper.toAuditLogs
import org.example.data.source.local.csv.utils.mapper.toCsvRow
import org.example.data.source.local.csv.utils.mapper.toCsvRows
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.utils.toInstant
import java.util.*
import kotlin.test.Test

class AuditLogCsvMapperKtTest {
    @Test
    fun `toCsvRow should return a CSV row when an audit log given`() {
        val audiLogID = UUID.randomUUID().toString()
        val currentTime = Clock.System.now()
        val auditLog =
            createAuditLog(
                id = audiLogID,
                userId = "1",
                action = "user abc changed task XYZ-001 from InProgress to InDevReview",
                createdAt = currentTime,
                entityType = AuditLogEntityType.TASK,
                entityId = "2",
                actionType = AuditLogActionType.UPDATE,
            )
        val expected =
            "$audiLogID,1,user abc changed task XYZ-001 from InProgress to InDevReview,${currentTime.toEpochMilliseconds()},TASK,2,UPDATE"

        val result = auditLog.toCsvRow()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toCsvRows should return a CSV rows when a list of audit log given`() {
        val audiLogID = UUID.randomUUID().toString()
        val audiLogID2 = UUID.randomUUID().toString()
        val currentTime = Clock.System.now()
        val auditLogs =
            listOf(
                createAuditLog(
                    id = audiLogID,
                    userId = "1",
                    action = "user abc changed task XYZ-001 from InProgress to InDevReview",
                    createdAt = currentTime,
                    entityType = AuditLogEntityType.TASK,
                    entityId = "2",
                    actionType = AuditLogActionType.UPDATE,
                ),
                createAuditLog(
                    id = audiLogID2,
                    userId = "2",
                    action = "user mno changed task XYZ-001 from InDevReview to InProgress",
                    createdAt = currentTime,
                    entityType = AuditLogEntityType.TASK,
                    entityId = "2",
                    actionType = AuditLogActionType.UPDATE,
                ),
            )
        val expected =
            listOf(
                "$audiLogID,1,user abc changed task XYZ-001 from InProgress to InDevReview,${currentTime.toEpochMilliseconds()},TASK,2,UPDATE",
                "$audiLogID2,2,user mno changed task XYZ-001 from InDevReview to InProgress,${currentTime.toEpochMilliseconds()},TASK,2,UPDATE",
            )
        val result = auditLogs.toCsvRows()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `toAuditLogs should return list of audit log when list of CSV rows are given`() {
        val lisOfCsvRows =
            listOf(
                "asd2-qwe2-asdw-wer1,1,user abc changed task XYZ-001 from InProgress to InDevReview,123456789,TASK,2,UPDATE",
                "hsd2-qw42-asdw-ukrr,2,user mno changed task XYZ-001 from InDevReview to InProgress,123456789,TASK,2,UPDATE",
            )
        val expected =
            listOf(
                createAuditLog(
                    id = "asd2-qwe2-asdw-wer1",
                    userId = "1",
                    action = "user abc changed task XYZ-001 from InProgress to InDevReview",
                    createdAt = 123456789L.toInstant(),
                    entityType = AuditLogEntityType.TASK,
                    entityId = "2",
                    actionType = AuditLogActionType.UPDATE,
                ),
                createAuditLog(
                    id = "hsd2-qw42-asdw-ukrr",
                    userId = "2",
                    action = "user mno changed task XYZ-001 from InDevReview to InProgress",
                    createdAt = 123456789L.toInstant(),
                    entityType = AuditLogEntityType.TASK,
                    entityId = "2",
                    actionType = AuditLogActionType.UPDATE,
                ),
            )
        val result = lisOfCsvRows.toAuditLogs()

        assertThat(result).isEqualTo(expected)
    }
}
