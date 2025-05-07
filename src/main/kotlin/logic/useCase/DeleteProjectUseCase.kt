package org.example.logic.useCase

import kotlinx.datetime.Clock
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.UnableToDeleteProjectException
import org.example.logic.utils.formattedString
import java.util.*

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val userUseCase: GetCurrentUserUseCase,
) {
    suspend operator fun invoke(projectId: String) {
        try {
            auditLogRepository.createAuditLog(saveAuditLog(projectId))

            projectRepository.deleteProject(projectId)
        } catch (e: Exception) {
            throw UnableToDeleteProjectException("Cannot delete project with existing tasks.")
        }
    }

    private suspend fun saveAuditLog(projectId: String): AuditLog {
        val user = userUseCase()
        val timestampNow = Clock.System.now()
        val auditLog = AuditLog(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            action = "${user.username} deleted project with id $projectId at ${timestampNow.formattedString()}",
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.PROJECT,
            entityId = projectId,
            actionType = AuditLogActionType.DELETE
        )
        return auditLog
    }

}
