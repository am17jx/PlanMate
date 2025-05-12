package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.repository.sources.remote.RemoteTaskDataSource
import org.example.data.source.remote.models.TaskDTO
import org.example.data.source.remote.mongo.utils.executeMongoOperation
import org.example.data.source.remote.mongo.utils.mapper.toTask
import org.example.data.source.remote.mongo.utils.mapper.toTaskDTO
import org.example.data.utils.Constants.ID
import org.example.data.utils.Constants.STATE_ID_FIELD
import org.example.logic.models.Task
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class MongoTaskDataSource(
    private val mongoClient: MongoCollection<TaskDTO>,
) : RemoteTaskDataSource {
    override suspend fun createTask(task: Task): Task = executeMongoOperation {
        mongoClient.insertOne(task.toTaskDTO())
        task
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun updateTask(updatedTask: Task): Task = executeMongoOperation {
        mongoClient.replaceOne(Filters.eq(ID, updatedTask.id.toHexString()), updatedTask.toTaskDTO())
        updatedTask
    }

    override suspend fun deleteTask(taskId: Uuid) {
        executeMongoOperation {
            mongoClient.deleteOne(Filters.eq(ID, taskId.toHexString()))
        }
    }

    override suspend fun getAllTasks(): List<Task> = executeMongoOperation {
        mongoClient.find().toList().map { it.toTask() }
    }

    override suspend fun getTaskById(taskId: Uuid): Task? = executeMongoOperation {
        mongoClient.find(Filters.eq(ID, taskId.toHexString())).firstOrNull()?.toTask()
    }

    override suspend fun getTasksByProjectState(stateId: Uuid): List<Task> = executeMongoOperation {
        mongoClient.find(
            Filters.eq(STATE_ID_FIELD, stateId.toHexString())
        ).toList().map { it.toTask() }
    }
}
