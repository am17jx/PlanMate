package org.example.data.source.remote.contract

import org.example.logic.models.State
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RemoteTaskStateDataSource {
    suspend fun createTaskState(taskState: State): State

    suspend fun updateTaskState(updatedTaskState: State): State

    suspend fun deleteTaskState(taskStateId: Uuid)

    suspend fun getProjectTaskStates(taskStateIds: List<Uuid>): List<State>

    suspend fun getTaskStateById(taskStateId: Uuid): State?
}
