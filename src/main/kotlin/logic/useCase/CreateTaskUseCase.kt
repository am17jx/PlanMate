package logic.useCase

import org.example.logic.models.*
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.useCase.CreateAuditLogUseCase
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.utils.*
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateTaskUseCase(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val createAuditLogUseCase: CreateAuditLogUseCase,
    private val projectStateRepository: ProjectStateRepository
) {
    suspend operator fun invoke(
        name: String,
        projectId: String,
        stateId: String,
    ): Task {
        verifyNoBlankInputs(name, projectId, stateId)
        val state = verifyProjectAndStateExist(projectId, stateId)
        return createAndLogTask(name, projectId, stateId, state.title, getCurrentUserUseCase())
    }

    private suspend fun createAndLogTask(
        taskName: String,
        projectId: String,
        stateId: String,
        stateName: String,
        loggedInUser: User,
    ): Task {
        val taskId = Uuid.random().getCroppedId()
        val taskAuditLog = createAuditLogUseCase.logCreation(
            entityId = taskId,
            entityName = taskName,
            entityType = AuditLog.EntityType.TASK
        )
        val newTask =
            Task(
                id = taskId,
                name = taskName,
                stateId = stateId,
                stateName = stateName,
                projectId = projectId,
                addedBy = loggedInUser.id,
                auditLogsIds = listOf(taskAuditLog.id),
            )
        taskRepository.createTask(newTask)
        return newTask
    }

    private suspend fun verifyProjectAndStateExist(
        projectId: String,
        stateId: String,
    ) : State{
       return  projectRepository.getProjectById(projectId)?.let { project ->
            if (project.projectStateIds.none { it == stateId }) throw TaskStateNotFoundException()
           projectStateRepository.getProjectStateById(stateId)
        } ?: throw ProjectNotFoundException()
    }

    private fun verifyNoBlankInputs(
        name: String,
        projectId: String,
        stateId: String,
    ) {
        when {
            name.isBlank() -> throw BlankInputException()
            projectId.isBlank() -> throw BlankInputException()
            stateId.isBlank() -> throw BlankInputException()
        }
    }


}
