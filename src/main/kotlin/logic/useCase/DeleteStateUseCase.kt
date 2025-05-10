package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.TaskStateRepository
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.TaskDeletionFailedException


class DeleteStateUseCase(
    private val taskStateRepository: TaskStateRepository,
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
        if (project.tasksStatesIds.size == 1) throw TaskDeletionFailedException()
        val projectStateTasks =
            taskRepository.getAllTasks().filter { it.projectId == projectId && it.stateId == stateId }
        for (task in projectStateTasks) {
            taskRepository.deleteTask(task.id)
        }
        taskStateRepository.deleteTaskState(stateId)
        projectRepository.updateProject(project.copy(tasksStatesIds = updatedStates))

    }

    private fun removeState(
        project: Project,
        stateId: String,
    ): List<String> = project.tasksStatesIds.filter { it != stateId }

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
