package logic.useCase

package org.example.logic.useCase

import org.example.logic.entities.User
import org.example.logic.entities.UserRole
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.utils.BlankInputException
import java.time.LocalDateTime

class DeleteTasksByProjectUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(user: User, projectId: String) {
        val tasks = taskRepository.getTasksByProjectId(projectId)
            .takeUnless { it.isNullOrEmpty() }
            ?: throw BlankInputException("No tasks found for project ID: $projectId")

        require(user.role == UserRole.ADMIN) {
            "Only admins can delete all tasks of a project."
        }

        tasks.forEach { task ->
            taskRepository.deleteTask(task.id)
            auditLogRepository.log(
                "User ${user.username} deleted task ${task.id} from project $projectId at ${LocalDateTime.now()}"
            )
        }
    }
}