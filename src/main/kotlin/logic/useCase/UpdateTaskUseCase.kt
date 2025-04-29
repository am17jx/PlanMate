package org.example.logic.usecases

import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.TaskRepository

class UpdateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val auditLogRepository: AuditLogRepository
) {
    operator fun invoke(taskId: String, updatedTask: Task): Task {
        TODO("Not yet implemented")
    }
}