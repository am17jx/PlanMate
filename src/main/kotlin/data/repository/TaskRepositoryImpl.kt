package org.example.data.repository

import org.example.data.repository.sources.remote.RemoteTaskDataSource
import org.example.data.repository.mapper.mapExceptionsToDomainException
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.utils.*

class TaskRepositoryImpl(
    private val remoteTaskDataSource: RemoteTaskDataSource
): TaskRepository {
    override suspend fun createTask(task: Task): Task {
        return mapExceptionsToDomainException(TaskCreationFailedException()) {
             remoteTaskDataSource.createTask(task)
        }
    }

    override suspend fun updateTask(updatedTask: Task): Task {
        return  mapExceptionsToDomainException(TaskNotChangedException()) {
         remoteTaskDataSource.updateTask(updatedTask)}
    }

    override suspend fun deleteTask(taskId: String) {
        return  mapExceptionsToDomainException(TaskDeletionFailedException()) {
         remoteTaskDataSource.deleteTask(taskId)}
    }

    override suspend fun getAllTasks(): List<Task> {
        return  mapExceptionsToDomainException(NoTasksFoundException()) {
         remoteTaskDataSource.getAllTasks()}
    }

    override suspend fun getTaskById(taskId: String): Task? {
        return  mapExceptionsToDomainException(NoTaskFoundException()) {
         remoteTaskDataSource.getTaskById(taskId)
        }
    }

    override suspend fun deleteTasksByStateId(stateId: String, projectId: String) {
        return  mapExceptionsToDomainException(TaskStateNotFoundException()) {
         remoteTaskDataSource.deleteTasksByStateId(stateId,projectId)
    }
    }
}