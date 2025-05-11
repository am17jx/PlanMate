package org.example.data.source.remote.contract

import org.example.logic.models.State

interface RemoteProjectStateDataSource {
    suspend fun createProjectState(projectState: State): State

    suspend fun updateProjectState(updatedProjectState: State): State

    suspend fun deleteProjectState(projectStateId: String)

    suspend fun getProjectStates(projectStatesIds:List<String>): List<State>

    suspend fun getProjectStateById(projectStateId: String): State?
}