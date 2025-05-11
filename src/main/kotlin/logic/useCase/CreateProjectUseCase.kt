package org.example.logic.useCase

import org.example.logic.models.AuditLog
import org.example.logic.models.Project
import org.example.logic.models.ProjectState
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val createAuditLogUseCase: CreateAuditLogUseCase,
    private val projectStateRepository: ProjectStateRepository,
    private val validation: Validation,
    ){
    suspend operator fun invoke(projectName: String): Project {
        validation.validateProjectNameOrThrow(projectName)
        return projectRepository.createProject(
            Project(
                name = projectName
            )
        ).also { project ->
            createLog(project.id, projectName)
            createDefaultStates(project.id)
        }
    }

    private suspend fun createLog(projectId: Uuid, projectName: String) {
        createAuditLogUseCase.logCreation(
            entityId = projectId,
            entityName = projectName,
            entityType = AuditLog.EntityType.PROJECT,
        )
    }

    private suspend fun createDefaultStates(projectId: Uuid) =
        listOf(
            projectStateRepository.createProjectState(ProjectState(title = DEFAULT_TO_DO_STATE_NAME, projectId = projectId)),
            projectStateRepository.createProjectState(ProjectState(title = DEFAULT_IN_PROGRESS_STATE_NAME, projectId = projectId)),
            projectStateRepository.createProjectState(ProjectState(title = DEFAULT_DONE_STATE_NAME, projectId = projectId)),
        ).map { it.id }

    companion object {
        const val DEFAULT_TO_DO_STATE_NAME = "To Do"
        const val DEFAULT_IN_PROGRESS_STATE_NAME = "In Progress"
        const val DEFAULT_DONE_STATE_NAME = "Done"
    }
}
