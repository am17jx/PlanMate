package org.example.logic.repositries

import org.example.logic.models.Task

interface TaskRepository {
    fun createTask(task: Task): Task
    fun updateTask(updatedTask: Task): Task
    fun deleteTask(taskId: String)
    fun getAllTasks(): List<Task>
    fun getTaskById(taskId: String): Task?
}
