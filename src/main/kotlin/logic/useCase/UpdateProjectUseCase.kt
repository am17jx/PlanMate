package org.example.logic.useCase

import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.utils.*
import java.util.*

class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val authenticationRepository: AuthenticationRepository

) {
    operator fun invoke(updatedProject: Project): Project {
        if (updatedProject.name.isEmpty()) throw BlankInputException("project name shouldn't be empty")
        val originalProject = currentOriginalProject(updatedProject)
        val currentUser = currentUser()
        if (currentUser.role != UserRole.ADMIN) {
            throw UnauthorizedException("Only admins can update projects.")
        }
        saveUpdatedProject(originalProject, updatedProject, currentUser)
        return updatedProject

    }

    private fun currentUser(): User {
        return authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException("No logged-in user found")
    }

    private fun currentOriginalProject(
        updatedProject: Project
    ): Project {
        return projectRepository.getProjectById(updatedProject.id)
            ?: throw ProjectNotFoundException("Project not found")
    }

    private fun saveUpdatedProject(project: Project, updatedProject: Project, currentUser: User): Project {
        val fullyUpdatedProject = projectRepository.updateProject(updatedProject)
        val auditLog = saveAuditLog(project, updatedProject, currentUser)
        //auditLog?.let { fullyUpdatedProject.auditLogsIds.add(it.id) }
        return fullyUpdatedProject

    }

    private fun saveAuditLog(project: Project, updatedProject: Project, currentUser: User): AuditLog? {
        val auditAction = when {
            project.name != updatedProject.name ->
                "${currentUser.username} changed Project name from ${project.name} to ${updatedProject.name}"
            project.states.size > updatedProject.states.size ->
                "${currentUser.username} deleted a state"
            project.states.size < updatedProject.states.size ->
                "${currentUser.username} added a state"
            else ->
                "${currentUser.username} updated states from to"
        }

        val auditLog = AuditLog(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            action = auditAction,
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.PROJECT,
            entityId = project.id,
            actionType = AuditLogActionType.UPDATE
        )
        return auditLogRepository.createAuditLog(auditLog)?:throw ProjectNotChangedException("")
    }

}