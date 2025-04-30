package logic.useCase


import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.utils.BlankInputException
import java.time.LocalDateTime

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository
) {
    fun execute(user: User, taskId: String, projectId: String) {
        val task = taskRepository.getTaskById(taskId)
            ?: throw BlankInputException("Task with ID $taskId not found")

        require(
            user.role == UserRole.ADMIN || task.projectId == projectId
        ) { "User does not have permission to delete this task." }

        taskRepository.deleteTask(taskId)

        auditLogRepository.log(
            "User ${user.username} deleted task $taskId from project $projectId at ${LocalDateTime.now()}"
        )
    }
}