package org.example.logic.command

import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository


class TaskUpdateCommand(
    private val taskRepository: TaskRepository,
    private val newTask: Task,
    private val originalTask: Task
) : Command {

    private var updatedTask: Task? = null

    override fun execute() {
        updatedTask = taskRepository.updateTask(newTask)
    }

    override fun undo() {
        updatedTask?.let { taskRepository.updateTask(originalTask) }
    }

}