package org.example.data.repository

import org.example.data.source.remote.contract.RemoteProjectStateDataSource
import org.example.logic.models.State
import org.example.logic.repositries.ProjectStateRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProjectStateRepositoryImpl(
    private val remoteProjectStateDataSource: RemoteProjectStateDataSource,
) : ProjectStateRepository {
    override suspend fun createProjectState(projectState: State): State = remoteProjectStateDataSource.createProjectState(projectState)

    override suspend fun updateProjectState(updatedProjectState: State): State =
        remoteProjectStateDataSource.updateProjectState(updatedProjectState)

    override suspend fun deleteProjectState(projectStateId: Uuid) {
        remoteProjectStateDataSource.deleteProjectState(projectStateId)
    }

    override suspend fun getProjectStates(projectStatesIds: List<Uuid>): List<State> =
        remoteProjectStateDataSource.getProjectStates(projectStatesIds)

    override suspend fun getProjectStateById(projectStateId: Uuid): State? =
        remoteProjectStateDataSource.getProjectStateById(projectStateId)
}
