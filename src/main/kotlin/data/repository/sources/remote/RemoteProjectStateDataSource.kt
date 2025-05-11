package org.example.data.source.remote.contract

import org.example.logic.models.State
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RemoteProjectStateDataSource {
    suspend fun createProjectState(projectState: State): State

    suspend fun updateProjectState(updatedProjectState: State): State

    suspend fun deleteProjectState(projectStateId: Uuid)

    suspend fun getProjectStates(projectStatesIds: List<Uuid>): List<State>

    suspend fun getProjectStateById(projectStateId: Uuid): State?
}
