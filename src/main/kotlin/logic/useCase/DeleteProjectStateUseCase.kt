package org.example.logic.useCase

import org.example.logic.models.AuditLog
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.Constants
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class DeleteProjectStateUseCase(
    private val projectStateRepository: ProjectStateRepository,
    private val taskRepository: TaskRepository,
    private val getProjectTasksUseCase: GetProjectTasksUseCase,
    private val getProjectStatesUseCase: GetProjectStatesUseCase,
    private val createAuditLogUseCase: CreateAuditLogUseCase
) {
    suspend operator fun invoke(
        stateId: Uuid,
        projectId: Uuid,
    ) {
        getProjectTasksUseCase(projectId).filter { it.stateId == stateId }.forEach { task ->
            taskRepository.deleteTask(task.id)
        }.also {
            val oldStates = getProjectStatesUseCase(projectId)
            createAuditLogUseCase.logUpdate(
                entityType = AuditLog.EntityType.PROJECT,
                entityId = projectId,
                entityName = "",
                fieldChange = AuditLog.FieldChange(
                    fieldName = Constants.FIELD_STATES,
                    oldValue = oldStates.joinToString(separator = ", ") { it.title },
                    newValue = oldStates.filter { it.id == stateId }.joinToString(separator = ", ") { it.title },
                )
            ).also {
                projectStateRepository.deleteProjectState(stateId)
            }
        }
    }

}
