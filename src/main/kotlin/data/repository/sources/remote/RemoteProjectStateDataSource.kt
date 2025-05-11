package org.example.data.source.remote.contract

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.example.logic.models.ProjectState

@OptIn(ExperimentalUuidApi::class)
interface RemoteProjectStateDataSource {
    suspend fun createProjectState(projectState: ProjectState): ProjectState

    suspend fun updateProjectState(updatedProjectProjectState: ProjectState): ProjectState

    suspend fun deleteProjectState(projectStateId: Uuid)

    suspend fun getProjectStates(projectId:Uuid): List<ProjectState>

    suspend fun getProjectStateById(projectStateId: Uuid): ProjectState?
}
