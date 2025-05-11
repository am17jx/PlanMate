package org.example.logic.useCase.updateProject

import org.example.logic.models.*
import org.example.logic.models.AuditLog.FieldChange.Companion.detectChanges
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotChangedException
import org.example.logic.utils.ProjectNotFoundException
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val createAuditLogUseCase: CreateAuditLogUseCase
) {
    suspend operator fun invoke(updatedProject: Project): Project {
        if (updatedProject.name.isEmpty()) throw BlankInputException()
        val originalProject = currentOriginalProject(updatedProject)
        detectChanges(updatedProject, originalProject)
        return saveUpdatedProject(originalProject, updatedProject)
    }

    private suspend fun saveUpdatedProject(
        originalProject: Project,
        newProject: Project
    ): Project {
        val logsIds = newProject.detectChanges(originalProject).map { change ->
            createAuditLogUseCase.logUpdate(
                entityId = newProject.id,
                entityName = newProject.name,
                entityType = AuditLog.EntityType.PROJECT,
                fieldChange = change
            ).id
        }
        projectRepository.updateProject(newProject.copy(auditLogsIds = newProject.auditLogsIds.plus(logsIds)))
        return newProject
    }



    private fun detectChanges(originalProject: Project, newProject: Project) {
        if ((originalProject.name == newProject.name) && (originalProject.projectStateIds.toSet() == newProject.projectStateIds.toSet())) throw ProjectNotChangedException()
    }

    private suspend fun currentOriginalProject(
        updatedProject: Project
    ): Project {
        return projectRepository.getProjectById(updatedProject.id) ?: throw ProjectNotFoundException()
    }

}
