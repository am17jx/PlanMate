package org.example.logic.useCase

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository

class GetTaskByIdUseCase(
    private val taskRepository: TaskRepository,
) {
    operator fun invoke(taskId:String):Task{
        TODO("Not yet implemented")
    }
}