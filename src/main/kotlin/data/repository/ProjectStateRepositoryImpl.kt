package org.example.data.repository

import org.example.data.source.remote.contract.RemoteProjectStateDataSource
import org.example.logic.models.State
import org.example.logic.repositries.ProjectStateRepository

class ProjectStateRepositoryImpl(
    private val remoteProjectStateDataSource: RemoteProjectStateDataSource
) : ProjectStateRepository {
    override suspend fun createTaskState(taskState: State): State {
        return remoteProjectStateDataSource.createTaskState(taskState)
    }

    override suspend fun updateTaskState(updatedTaskState: State): State {
        return remoteProjectStateDataSource.updateTaskState(updatedTaskState)
    }

    override suspend fun deleteTaskState(taskStateId: String) {
        remoteProjectStateDataSource.deleteTaskState(taskStateId)
    }

    override suspend fun getProjectTaskStates(taskStateIds: List<String>): List<State> {
        return remoteProjectStateDataSource.getProjectTaskStates(taskStateIds)
    }

    override suspend fun getProjectStateById(taskStateId: String): State? {
        return remoteProjectStateDataSource.getTaskStateById(taskStateId)
    }
}