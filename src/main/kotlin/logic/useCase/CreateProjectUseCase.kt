package org.example.logic.useCase

import org.example.logic.models.*
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskStateRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val createAuditLogUseCase: CreateAuditLogUseCase,
    private val taskStateRepository: TaskStateRepository,
    private val validation: Validation,
) {
    suspend operator fun invoke(projectName: String): Project {
        validation.validateProjectNameOrThrow(projectName)

        return createAndLogProject(projectName)
    }

    private suspend fun createAndLogProject(projectName: String): Project {
        val projectId = Uuid.random()
        val audit =
            createAuditLogUseCase.logCreation(
                entityId = projectId,
                entityName = projectName,
                entityType = AuditLog.EntityType.PROJECT,
            )
        val newProject =
            Project(
                id = projectId,
                name = projectName,
                tasksStatesIds = getDefaultStates(),
                auditLogsIds = listOf(audit.id),
            )

        projectRepository.createProject(newProject)
        return newProject
    }

    private suspend fun getDefaultStates() =
        listOf(
            taskStateRepository.createTaskState(State(title = DEFAULT_TO_DO_STATE_NAME)),
            taskStateRepository.createTaskState(State(title = DEFAULT_IN_PROGRESS_STATE_NAME)),
            taskStateRepository.createTaskState(State(title = DEFAULT_DONE_STATE_NAME)),
        ).map { it.id }

    companion object {
        const val DEFAULT_TO_DO_STATE_NAME = "To Do"
        const val DEFAULT_IN_PROGRESS_STATE_NAME = "In Progress"
        const val DEFAULT_DONE_STATE_NAME = "Done"
    }
}
