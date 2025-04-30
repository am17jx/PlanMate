package org.example.data.repository

import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository

class TaskRepositoryImpl(
    private val localTaskDataSource: LocalTaskDataSource
): TaskRepository {
    override fun createTask(task: Task): Task {
        return localTaskDataSource.createTask(task)
    }

    override fun updateTask(updatedTask: Task): Task {
        return localTaskDataSource.updateTask(updatedTask)
    }

    override fun deleteTask(taskId: String) {
        return localTaskDataSource.deleteTask(taskId)
    }

    override fun getAllTasks(): List<Task> {
        return localTaskDataSource.getAllTasks()
    }

    override fun getTaskById(taskId: String): Task? {
        return localTaskDataSource.getTaskById(taskId)
    }
}