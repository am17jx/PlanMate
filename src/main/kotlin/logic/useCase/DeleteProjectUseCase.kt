package org.example.logic.useCase

import kotlinx.datetime.Clock
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.utils.formattedString
import java.util.*

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val userUseCase: GetCurrentUserUseCase,
) {
    suspend operator fun invoke(projectId: String) {
        auditLogRepository.createAuditLog(saveAuditLog(projectId))

        projectRepository.deleteProject(projectId)
    }

    private suspend fun saveAuditLog(projectId: String): AuditLog {
        val user = userUseCase()
        val currentTime = Clock.System.now()
        val auditLog =
            AuditLog(
                id = UUID.randomUUID().toString(),
                userId = user.id,
                action = "${user.username} deleted project with id $projectId at ${currentTime.formattedString()}",
                createdAt = currentTime,
                entityType = AuditLogEntityType.PROJECT,
                entityId = projectId,
                actionType = AuditLogActionType.DELETE,
            )
        return auditLog
    }
}
