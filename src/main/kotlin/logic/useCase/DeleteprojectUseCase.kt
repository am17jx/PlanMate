package logic.useCase

import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.utils.ProjectNotFoundException
import java.time.LocalDateTime

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository
) {
    fun execute(user: User, projectId: String) {
        val project = projectRepository.getProjectById(projectId)
            ?: throw ProjectNotFoundException("Project with ID $projectId not found.")

        require(user.role == UserRole.ADMIN) {
            "Only admins can delete projects."
        }

        taskRepository.getTasksByProjectId(projectId).forEach { task ->
            taskRepository.deleteTask(task.id)
            auditLogRepository.log(
                "User ${user.username} deleted task ${task.id} from project $projectId at ${LocalDateTime.now()}"
            )
        }

        projectRepository.deleteProject(projectId)

        auditLogRepository.log(
            "User ${user.username} deleted project $projectId at ${LocalDateTime.now()}"
        )
    }
}