package logic.useCase

import kotlinx.datetime.Clock
import org.example.logic.entities.AuditLog
import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.exceptions.BlankInputException
import org.example.logic.exceptions.TaskNotFoundException
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.TaskRepository

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(user: User, taskId: String, projectId: String) {
        if (taskId.isBlank()) throw BlankInputException("Task ID must not be blank")
        if (projectId.isBlank()) throw BlankInputException("Project ID must not be blank")

        val task = taskRepository.getTaskById(taskId)
            ?: throw TaskNotFoundException("Task with ID $taskId not found")

        require(user.role == UserRole.ADMIN || task.projectId == projectId) {
            "User does not have permission to delete this task."
        }

        taskRepository.deleteTask(taskId)

        auditLogRepository.createLog(
            AuditLog(
                action = "DELETE_TASK",
                performedBy = user.username,
                details = "Deleted task $taskId from project $projectId",
                timestamp = Clock.System.now()
            )
        )
    }
}
