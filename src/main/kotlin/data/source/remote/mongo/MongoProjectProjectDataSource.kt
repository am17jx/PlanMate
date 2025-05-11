package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.remote.models.StateDTO
import org.example.data.source.remote.contract.RemoteProjectStateDataSource
import org.example.data.source.remote.mongo.utils.mapper.toState
import org.example.data.source.remote.mongo.utils.mapper.toStateDTO
import org.example.data.utils.Constants.ID
import org.example.logic.models.State
import org.example.logic.utils.TaskCreationFailedException
import org.example.logic.utils.TaskDeletionFailedException
import org.example.logic.utils.TaskNotChangedException
import org.example.logic.utils.TaskNotFoundException
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class MongoProjectProjectDataSource(
    private val mongoClient: MongoCollection<StateDTO>
) : RemoteProjectStateDataSource {

    override suspend fun createProjectState(projectState: State): State {
        try {
            mongoClient.insertOne(projectState.toStateDTO())
            return projectState
        } catch (e: Exception) {
            throw TaskCreationFailedException()
        }
    }

    override suspend fun updateProjectState(updatedProjectState: State): State {
        try {
            mongoClient.replaceOne(Filters.eq(ID, updatedProjectState.id.toHexString()), updatedProjectState.toStateDTO())
            return updatedProjectState
        } catch (e: Exception) {
            throw TaskNotChangedException()
        }
    }

    override suspend fun deleteProjectState(projectStateId: String) {
        try {
            mongoClient.deleteOne(Filters.eq(ID, projectStateId))
        } catch (e: Exception) {
            throw TaskDeletionFailedException()
        }
    }

    override suspend fun getProjectStateById(projectStateId: String): State? {
        try {
            return mongoClient.find(Filters.eq(ID, projectStateId)).firstOrNull()?.toState()
        } catch (e: Exception) {
            throw TaskNotFoundException()
        }
    }

    override suspend fun getProjectStates(projectStatesIds: List<String>): List<State> =
        try {
            mongoClient
                .find(Filters.`in`(ID, projectStatesIds))
                .toList()
                .map { it.toState() }
        } catch (e: Exception) {
            throw TaskNotFoundException()
        }
}
