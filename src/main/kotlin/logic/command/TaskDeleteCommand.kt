package org.example.logic.command

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository


class TaskDeleteCommand(
    private val taskRepository: TaskRepository,
    private val task: Task,
) : Command {

    private var deletedTask: Task? = null

    override fun execute() {
        deletedTask = task
        taskRepository.deleteTask(task.id)

    }

    override fun undo() {
        deletedTask?.let { taskRepository.createTask(it) }
    }

}