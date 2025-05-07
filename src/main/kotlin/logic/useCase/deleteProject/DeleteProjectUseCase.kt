package org.example.logic.useCase.deleteProject

import kotlinx.datetime.Clock
import org.example.logic.command.Command
import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionalCommand
import org.example.logic.models.AuditLog
import org.example.logic.models.AuditLogActionType
import org.example.logic.models.AuditLogEntityType
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.useCase.deleteTask.DeleteTaskCommand
import org.example.logic.utils.UnableToDeleteProjectException
import org.example.logic.utils.formattedString
import java.util.*

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val userUseCase: GetCurrentUserUseCase,
    private val getProjectTasksUseCase: GetProjectTasksUseCase,
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(projectId: String) {
        val command: MutableList<Command> = mutableListOf()

        val auditCommand = CreateAuditLogCommand(auditLogRepository, saveAuditLog(projectId))

        getProjectTasksUseCase(projectId).forEach {
            command.add(DeleteTaskCommand(taskRepository, it))
        }

        val deleteProject = DeleteProjectCommand(projectRepository, projectId)

        command.add(deleteProject)
        command.add(auditCommand)
        TransactionalCommand(
            command,
            UnableToDeleteProjectException("Cannot delete project with existing tasks.")
        ).execute()
    }

    private fun saveAuditLog(projectId: String): AuditLog {
        val timestampNow = Clock.System.now()
        val auditLog = AuditLog(
            id = UUID.randomUUID().toString(),
            userId = userUseCase().id,
            action = "${userUseCase().username} deleted project with id $projectId at ${timestampNow.formattedString()}",
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.PROJECT,
            entityId = projectId,
            actionType = AuditLogActionType.DELETE
        )
        return auditLog
    }

}
