package org.example.logic.usecase

import org.example.logic.models.Task
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository

class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(
        name: String,
        projectId: String,
        stateId: String,
    ): Task {
        TODO("Not yet implemented")
    }
}