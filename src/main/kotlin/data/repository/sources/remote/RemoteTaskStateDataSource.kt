package org.example.data.source.remote.contract

import org.example.logic.models.State
import org.example.logic.models.Task

interface RemoteTaskStateDataSource {
    suspend fun createTaskState(taskState: State): State

    suspend fun updateTaskState(updatedTaskState: State): State

    suspend fun deleteTaskState(taskStateId: String)

    suspend fun getProjectTaskStates(projectId: String): List<State>

    suspend fun getTaskStateById(taskStateId: String): State?
}