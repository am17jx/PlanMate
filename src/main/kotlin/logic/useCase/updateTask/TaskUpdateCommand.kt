package org.example.logic.useCase.updateTask

import org.example.logic.command.Command
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository


class TaskUpdateCommand(
    private val taskRepository: TaskRepository,
    private val newTask: Task,
    private val originalTask: Task
) : Command {

    private var updatedTask: Task? = null

    override suspend fun execute() {
        updatedTask = taskRepository.updateTask(newTask)
    }

    override suspend fun undo() {
        updatedTask?.let { taskRepository.updateTask(originalTask) }
    }

}