package org.example.data.source.local.contract

import org.example.logic.models.Task

interface LocalTaskDataSource {
    fun createTask(task: Task): Task
    fun updateTask(updatedTask: Task): Task
    fun deleteTask(taskId: String)
    fun getAllTasks(): List<Task>
    fun getTaskById(taskId: String): Task?
}