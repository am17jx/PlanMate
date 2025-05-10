package org.example.data.repository

import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.source.remote.contract.RemoteTaskDataSource
import org.example.data.source.remote.contract.RemoteTaskStateDataSource
import org.example.logic.models.State
import org.example.logic.models.Task
import org.example.logic.repositries.TaskRepository
import org.example.logic.repositries.TaskStateRepository

class TaskStateRepositoryImpl(
    private val remoteTaskStateDataSource: RemoteTaskStateDataSource
) : TaskStateRepository {
    override suspend fun createTaskState(taskState: State): State {
        return remoteTaskStateDataSource.createTaskState(taskState)
    }

    override suspend fun updateTaskState(updatedTaskState: State): State {
        return remoteTaskStateDataSource.updateTaskState(updatedTaskState)
    }

    override suspend fun deleteTaskState(taskStateId: String) {
        remoteTaskStateDataSource.deleteTaskState(taskStateId)
    }

    override suspend fun getProjectTaskStates(taskStateIds: List<String>): List<State> {
        return remoteTaskStateDataSource.getProjectTaskStates(taskStateIds)
    }

    override suspend fun getTaskStateById(taskStateId: String): State? {
        return remoteTaskStateDataSource.getTaskStateById(taskStateId)
    }
}