package org.example.logic.command

import org.example.logic.models.AuditLog
import org.example.logic.repositries.AuditLogRepository

class CreateAuditLogCommand(
    private val auditLogRepository: AuditLogRepository,
    private val auditLog: AuditLog
) : Command {

    private var createdLog: AuditLog? = null

    override fun execute() {
        createdLog = auditLogRepository.createAuditLog(auditLog)
    }

    override fun undo() {
        createdLog?.let { auditLogRepository.deleteAuditLog(it.id) }
    }

    fun getCreatedLog(): AuditLog? {
        return createdLog
    }

}
