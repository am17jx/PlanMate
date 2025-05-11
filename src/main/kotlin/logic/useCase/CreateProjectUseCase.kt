package org.example.logic.useCase

import org.example.logic.models.*
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectCreationFailedException
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val createAuditLogUseCase: CreateAuditLogUseCase,
    private val projectStateRepository: ProjectStateRepository,
){
    suspend operator fun invoke(projectName: String): Project {
        checkInputValidation(projectName)

        return createAndLogProject(projectName)
    }

    private suspend fun createAndLogProject(projectName: String): Project {
        val projectId = Uuid.random().getCroppedId()
        val audit = createAuditLogUseCase.logCreation(
            entityId = projectId,
            entityName = projectName,
            entityType = AuditLog.EntityType.PROJECT
        )
        val newProject =
            Project(
                id = projectId,
                name = projectName,
                projectStateIds = getDefaultStates(),
                auditLogsIds = listOf(audit.id),
            )

        projectRepository.createProject(newProject)
        return newProject
    }

    private suspend fun getDefaultStates() =
        listOf(
            projectStateRepository.createTaskState(State(Uuid.random().getCroppedId(), DEFAULT_TO_DO_STATE_NAME)),
            projectStateRepository.createTaskState(State(Uuid.random().getCroppedId(), DEFAULT_IN_PROGRESS_STATE_NAME)),
            projectStateRepository.createTaskState(State(Uuid.random().getCroppedId(), DEFAULT_DONE_STATE_NAME)),
        ).map { it.id }

    private fun checkInputValidation(projectName: String) {
        when {
            projectName.isBlank() -> throw BlankInputException()
            projectName.length > 16 -> throw ProjectCreationFailedException()
        }
    }

    companion object {
        const val DEFAULT_TO_DO_STATE_NAME = "To Do"
        const val DEFAULT_IN_PROGRESS_STATE_NAME = "In Progress"
        const val DEFAULT_DONE_STATE_NAME = "Done"
    }
}
