package org.example.data.repository

import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.source.remote.contract.RemoteTaskDataSource
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository

class TaskRepositoryImpl(
    private val remoteTaskDataSource: RemoteTaskDataSource
): TaskRepository {
    override suspend fun createTask(task: Task): Task {
        return remoteTaskDataSource.createTask(task)
    }

    override suspend fun updateTask(updatedTask: Task): Task {
        return remoteTaskDataSource.updateTask(updatedTask)
    }

    override suspend fun deleteTask(taskId: String) {
        return remoteTaskDataSource.deleteTask(taskId)
    }

    override suspend fun getAllTasks(): List<Task> {
        return remoteTaskDataSource.getAllTasks()
    }

    override suspend fun getTaskById(taskId: String): Task? {
        return remoteTaskDataSource.getTaskById(taskId)
    }

    override suspend fun deleteTasksByStateId(stateId: String, projectId: String) {
        return remoteTaskDataSource.deleteTasksByStateId(stateId,projectId)
    }
}