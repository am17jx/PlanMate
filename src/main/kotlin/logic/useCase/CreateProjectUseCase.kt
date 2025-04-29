package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val authenticationRepository: AuthenticationRepository,
) {
    operator fun invoke(projectName: String): Project {
        TODO("Not yet implemented")
    }
}
