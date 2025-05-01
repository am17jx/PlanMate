package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.Task

class GetStateNameUseCase(
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase
) {
    operator fun invoke(taskId: String): String? {
        val task: Task = getTaskByIdUseCase(taskId)
        val project: Project = getProjectByIdUseCase(task.projectId)
        return project.states.firstOrNull { it.id == task.stateId }?.title
    }
}