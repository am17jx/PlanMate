package logic.useCase

import org.example.logic.models.*
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.useCase.Validation
import org.example.logic.utils.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createAuditLogUseCase: CreateAuditLogUseCase,
    private val projectStateRepository: ProjectStateRepository,
    private val validation: Validation,
    ) {
    suspend operator fun invoke(
        name: String,
        projectId: Uuid,
        stateId: Uuid,
    ): Task {
        validation.validateInputNotBlankOrThrow(name)
        val state = getState(stateId)
        val currentUser = getCurrentUserUseCase()
        return taskRepository.createTask(
            Task(
                name = name,
                stateId = stateId,
                stateName = state.title,
                projectId = projectId,
                addedById = currentUser.id,
                addedByName = currentUser.username
            )
        ).also { task ->
            createAuditLogUseCase.logCreation(
                entityId = task.id,
                entityName = task.name,
                entityType = AuditLog.EntityType.TASK,
            )
        }
    }

    private suspend fun getState(
        stateId: Uuid,
    ): ProjectState = projectStateRepository.getProjectStateById(stateId).takeIf {
            it != null
        } ?: throw TaskStateNotFoundException()

}
