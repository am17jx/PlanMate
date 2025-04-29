package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository

class UpdateProjectUseCase(
    projectRepository: ProjectRepository,
    auditLogRepository: AuditLogRepository,
    authenticationRepository: AuthenticationRepository

) {
    operator fun invoke(updateProject: Project): Project {
        return updateProject}
}