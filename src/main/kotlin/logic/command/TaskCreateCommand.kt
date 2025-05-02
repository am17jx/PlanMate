package org.example.logic.command

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository


class TaskCreateCommand(
    private val taskRepository: TaskRepository,
    private val newTask: Task
) : Command {

    private var createdTask: Task? = null

    override fun execute() {
        createdTask = taskRepository.createTask(newTask)
    }

    override fun undo() {
        createdTask?.let { taskRepository.deleteTask(taskId = newTask.id) }
    }

}