package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.models.Task
import org.example.logic.utils.TaskStateNotFoundException

class GetStateNameUseCase(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase
) {
    suspend operator fun invoke(taskId: String): String {
        val task: Task = getTaskByIdUseCase(taskId)
        val project: Project = getProjectByIdUseCase(task.projectId)
        return getState(project,task.stateId).title
    }
    private fun getState(project: Project, stateId: String): State =
        project.states.find { it.id == stateId }
            ?: throw TaskStateNotFoundException()
}