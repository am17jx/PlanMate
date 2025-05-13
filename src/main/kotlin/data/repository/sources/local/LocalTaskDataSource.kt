package org.example.data.repository.sources.local

import org.example.logic.models.Task

interface LocalTaskDataSource {
    fun createTask(task: Task): Task
    fun updateTask(updatedTask: Task): Task
    fun deleteTask(taskId: String)
    fun getAllTasks(): List<Task>
    fun getTaskById(taskId: String): Task?
    fun deleteTasksByStateId(stateId: String,projectId:String)
}