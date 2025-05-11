package org.example.data.repository

import org.example.data.source.remote.contract.RemoteProjectStateDataSource
import org.example.logic.models.ProjectState
import org.example.logic.repositries.ProjectStateRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProjectStateRepositoryImpl(
    private val remoteProjectStateDataSource: RemoteProjectStateDataSource
) : ProjectStateRepository {
    override suspend fun createProjectState(projectState: ProjectState): ProjectState {
        return remoteProjectStateDataSource.createProjectState(projectState)
    }

    override suspend fun updateProjectState(updatedProjectProjectState: ProjectState): ProjectState {
        return remoteProjectStateDataSource.updateProjectState(updatedProjectProjectState)
    }

    override suspend fun deleteProjectState(projectStateId: Uuid) {
        remoteProjectStateDataSource.deleteProjectState(projectStateId.toHexString())
    }

    override suspend fun getProjectStates(projectId: Uuid): List<ProjectState> {
        return remoteProjectStateDataSource.getProjectStates(projectId.toHexString())
    }

    override suspend fun getProjectStateById(projectStateId: Uuid): ProjectState? {
        return remoteProjectStateDataSource.getProjectStateById(projectStateId.toHexString())
    }
}