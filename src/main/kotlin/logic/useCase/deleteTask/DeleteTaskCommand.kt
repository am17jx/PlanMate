package org.example.logic.useCase.deleteTask

import org.example.logic.command.Command
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository


class DeleteTaskCommand(
    private val taskRepository: TaskRepository,
    private val task: Task,
) : Command {
    override fun execute() {
        taskRepository.deleteTask(task.id)
    }

    override fun undo() {
        taskRepository.createTask(task)
    }
}
