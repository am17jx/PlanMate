package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.models.Task
import org.example.logic.repositries.TaskStateRepository
import org.example.logic.utils.StateNotFoundException

class GetProjectStatesUseCase(
    private val getProjectByIdUseCase: GetProjectByIdUseCase,
    private val taskStateRepository: TaskStateRepository
) {
    suspend operator fun invoke(projectId: String): List<State> {
        val project: Project = getProjectByIdUseCase(projectId)
        return taskStateRepository.getProjectTaskStates(project.tasksStatesIds)
    }
}