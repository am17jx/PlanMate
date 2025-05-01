package org.example.logic.useCase

import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionalUpdateProjectCommand
import org.example.logic.command.UpdateProjectCommand
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
        if (currentUser.role != UserRole.ADMIN) throw UnauthorizedException("Only admins can update projects.")

        return saveUpdatedProject(originalProject, updatedProject, currentUser)

    }

    private fun saveUpdatedProject(originalProject: Project, newProject: Project, currentUser: User): Project {
        val updatedProjectResult: Project

        val auditLog = saveAuditLog(originalProject, newProject, currentUser)
        val auditCommand = CreateAuditLogCommand(auditLogRepository, auditLog)
        val updateProjectCommand = UpdateProjectCommand(projectRepository, newProject, originalProject)

        val updateCommandTransaction = TransactionalUpdateProjectCommand(listOf(updateProjectCommand, auditCommand))
        try {
            updateCommandTransaction.execute()
            updatedProjectResult = newProject.copy(
                name = newProject.name,
                id = newProject.id,
                states = newProject.states,
                auditLogsIds = newProject.auditLogsIds.plus(auditCommand.getCreatedLog()?.id ?: "")
            )
        } catch (e: ProjectNotChangedException) {
            throw ProjectNotChangedException("Project Not changed: ${e.message}")

        }

        return updatedProjectResult

    }

    private fun saveAuditLog(project: Project, updatedProject: Project, currentUser: User): AuditLog {
        val actionBuilder = actionBuilder(project, updatedProject, currentUser)
        val auditLog = AuditLog(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            action = actionBuilder,
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.PROJECT,
            entityId = project.id,
            actionType = AuditLogActionType.UPDATE
        )
        return auditLog
    }

    private fun actionBuilder(project: Project, updatedProject: Project, currentUser: User) = when {
        project.name != updatedProject.name ->
            "${currentUser.username} changed Project name from ${project.name} to ${updatedProject.name}"

        project.states.size > updatedProject.states.size ->
            "${currentUser.username} deleted a state"

        project.states.size < updatedProject.states.size ->
            "${currentUser.username} added a state"

        else ->
            "${currentUser.username} updated a state"
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
}