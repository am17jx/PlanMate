package org.example.logic.useCase

import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.entities.User

class DeleteTasksByProjectUseCase(
    private val taskRepository: TaskRepository,
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(user: User, projectId: String) {
        TODO("Not yet implemented")
    }
}