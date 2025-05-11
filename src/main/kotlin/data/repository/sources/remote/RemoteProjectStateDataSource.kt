package org.example.data.source.remote.contract

import org.example.logic.models.ProjectState

interface RemoteProjectStateDataSource {
    suspend fun createProjectState(projectState: ProjectState): ProjectState

    suspend fun updateProjectState(updatedProjectProjectState: ProjectState): ProjectState

    suspend fun deleteProjectState(projectStateId: String)

    suspend fun getProjectStates(projectId:String): List<ProjectState>

    suspend fun getProjectStateById(projectStateId: String): ProjectState?
}