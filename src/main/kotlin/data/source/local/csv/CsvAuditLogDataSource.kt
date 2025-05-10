package org.example.data.source.local.csv

import org.example.data.repository.sources.local.LocalAuditLogDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.data.source.local.csv.utils.mapper.toAuditLogs
import org.example.data.source.local.csv.utils.mapper.toCsvRows
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogEntityType

class CsvAuditLogDataSource(private val csvReader: CSVReader, private val csvWriter: CSVWriter) :
    LocalAuditLogDataSource {
    private var audits = mutableListOf<AuditLog>()

    init {
        readCsvAuditLogs()
    }


    override fun saveAuditLog(auditLog: AuditLog): AuditLog {
        audits.add(auditLog)
        writeCsvAuditLogs()
        return auditLog
    }

    override fun deleteAuditLog(auditLogId: String) {
        audits.removeIf { it.id == auditLogId }
        writeCsvAuditLogs()
    }

    override fun getEntityLogs(entityId: String, entityType: AuditLogEntityType): List<AuditLog> {
        return audits.filter { it.entityId == entityId && it.entityType == entityType }
    }

    override fun getEntityLogByLogId(auditLogId: String): AuditLog? {
        return audits.firstOrNull { it.id == auditLogId }
    }

    private fun readCsvAuditLogs() {
        csvReader.readLines().toAuditLogs().let { updatedTasks ->
            audits = updatedTasks.toMutableList()
        }
    }

    private fun writeCsvAuditLogs() {
        csvWriter.writeLines(
            audits.toCsvRows()
        )
        readCsvAuditLogs()
    }
}