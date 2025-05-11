package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.ProjectStateRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskDeletionFailedException
import kotlin.uuid.ExperimentalUuidApi


@OptIn(ExperimentalUuidApi::class)
class DeleteStateUseCase(
    private val projectStateRepository: ProjectStateRepository,
    private val projectRepository: ProjectRepository,
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(
        stateId: String,
        projectId: String,
    ) {
        checkInputValidation(stateId, projectId)
        val project = getProject(projectId)
        val updatedStates = removeState(project, stateId)
        if (project.projectStateIds.size == 1) throw TaskDeletionFailedException()
        val projectStateTasks =
            taskRepository.getAllTasks().filter { it.projectId == projectId && it.stateId == stateId }
        for (task in projectStateTasks) {
            taskRepository.deleteTask(task.id)
        }
        projectStateRepository.deleteTaskState(stateId)
        projectRepository.updateProject(project.copy(projectStateIds = updatedStates))

    }

    private fun removeState(
        project: Project,
        stateId: String,
    ): List<String> = project.projectStateIds.filter { it != stateId }

    private suspend fun getProject(projectId: String): Project =
        projectRepository.getProjectById(projectId)
            ?: throw ProjectNotFoundException()


    private fun checkInputValidation(
        stateId: String,
        projectId: String,
    ) {
        when {
            stateId.isBlank() -> throw BlankInputException()
            projectId.isBlank() -> throw BlankInputException()
        }
    }

}
