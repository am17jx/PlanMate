package org.example.logic.useCase.creatTask

import org.example.logic.command.Command
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository


class TaskCreateCommand(
    private val taskRepository: TaskRepository,
    private val newTask: Task
) : Command {

    private var createdTask: Task? = null

    override suspend fun execute() {
        createdTask = taskRepository.createTask(newTask)
    }

    override suspend fun undo() {
        createdTask?.let { taskRepository.deleteTask(taskId = newTask.id) }
    }

}