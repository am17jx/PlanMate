package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.remote.contract.RemoteProjectStateDataSource
import org.example.data.source.remote.models.StateDTO
import org.example.data.source.remote.mongo.utils.mapper.toState
import org.example.data.source.remote.mongo.utils.mapper.toStateDTO
import org.example.data.utils.Constants.ID
import org.example.data.utils.Constants.PROJECT_ID
import org.example.logic.models.ProjectState
import org.example.logic.utils.TaskCreationFailedException
import org.example.logic.utils.TaskDeletionFailedException
import org.example.logic.utils.TaskNotChangedException
import org.example.logic.utils.TaskNotFoundException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MongoProjectStateDataSource(
    private val mongoClient: MongoCollection<StateDTO>
) : RemoteProjectStateDataSource {

    override suspend fun createProjectState(projectState: ProjectState): ProjectState {
        try {
            mongoClient.insertOne(projectState.toStateDTO())
            return projectState
        } catch (e: Exception) {
            throw TaskCreationFailedException()
        }
    }

    override suspend fun updateProjectState(updatedProjectProjectState: ProjectState): ProjectState {
        try {
            mongoClient.replaceOne(Filters.eq(ID, updatedProjectProjectState.id.toHexString()), updatedProjectProjectState.toStateDTO())
            return updatedProjectProjectState
        } catch (e: Exception) {
            throw TaskNotChangedException()
        }
    }

    override suspend fun deleteProjectState(projectStateId: Uuid) {
        try {
            mongoClient.deleteOne(Filters.eq(ID, projectStateId.toHexString()))
        } catch (e: Exception) {
            throw TaskDeletionFailedException()
        }
    }

    override suspend fun getProjectStateById(projectStateId: Uuid): State? {
        try {
            return mongoClient.find(Filters.eq(ID, projectStateId.toHexString())).firstOrNull()?.toState()
        } catch (e: Exception) {
            throw TaskNotFoundException()
        }
    }

    override suspend fun getProjectStates(projectId: Uuid): List<State> =
        try {
            mongoClient
                .find(Filters.eq(PROJECT_ID, projectId))
                .toList()
                .map { it.toState() }
        } catch (e: Exception) {
            throw TaskNotFoundException()
        }
}
