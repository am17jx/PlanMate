package org.example.logic.useCase

import org.example.logic.command.Command
import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionCommands
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import java.util.*

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val deleteTasksOfProjectUseCase: DeleteTasksOfProjectUseCase,
    private val userUseCase: GetCurrentUserUseCase
) {
    operator fun invoke(projectId: String){

        val auditLog = saveAuditLog(projectId)
        val auditCommand = CreateAuditLogCommand(auditLogRepository, auditLog)
        val deleteProject = DeleteProject(projectRepository,deleteTasksOfProjectUseCase, projectId)
        TransactionCommands(listOf(auditCommand, deleteProject))

    }
    private fun saveAuditLog(projectId: String): AuditLog {
        val auditLog = AuditLog(
            id = UUID.randomUUID().toString(),
            userId = userUseCase().id,
            action = "${userUseCase().username} deleted project with id $projectId",
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.PROJECT,
            entityId = projectId,
            actionType = AuditLogActionType.DELETE
        )
        return auditLog
    }

}

class DeleteProject(
    private val projectRepository: ProjectRepository,
    private val deleteTasksOfProjectUseCase: DeleteTasksOfProjectUseCase,
    private val projectId: String
): Command {
    override fun execute() {
        deleteTasksOfProjectUseCase(projectId)
        projectRepository.deleteProject(projectId)
    }
    override fun undo() {
        projectRepository.createProject(projectRepository.getProjectById(projectId)!!) }
}