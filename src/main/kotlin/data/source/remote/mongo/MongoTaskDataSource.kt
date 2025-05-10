package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.remote.mongo.utils.mapper.toTask
import org.example.data.source.remote.mongo.utils.mapper.toTaskDTO
import org.example.data.source.remote.models.TaskDTO
import org.example.data.repository.sources.remote.RemoteTaskDataSource
import org.example.data.utils.Constants.ID
import org.example.data.utils.Constants.STATE_ID_FIELD
import org.example.logic.models.Task
import org.example.logic.utils.*

class MongoTaskDataSource(
    private val mongoClient: MongoCollection<TaskDTO>
) : RemoteTaskDataSource {
    override suspend fun createTask(task: Task): Task {
        try {
            mongoClient.insertOne(task.toTaskDTO())
            return task
        } catch (e: Exception) {
            throw CreationItemFailedException("project creation failed ${e.message}")

        }
    }

    override suspend fun updateTask(updatedTask: Task): Task {
        try {
            mongoClient.replaceOne(Filters.eq(ID, updatedTask.id), updatedTask.toTaskDTO())
            return updatedTask
        } catch (e: Exception) {
            throw UpdateItemFailedException("project update failed ${e.message}")

        }
    }

    override suspend fun deleteTask(taskId: String) {
        try {
            mongoClient.deleteOne(Filters.eq(ID, taskId))
        } catch (e: Exception) {
            throw DeleteItemFailedException("project delete failed ${e.message}")
        }
    }

    override suspend fun getAllTasks(): List<Task> {
        try {
            return mongoClient.find().toList().map { it.toTask() }
        } catch (e: Exception) {
            throw GetItemsFailedException("tasks get failed ${e.message}")
        }

    }

    override suspend fun getTaskById(taskId: String): Task? {
        try {
            return mongoClient.find(Filters.eq(ID, taskId)).firstOrNull()?.toTask()
        } catch (e: Exception) {
            throw GetItemByIdFailedException("task get by id failed ${e.message}")
        }

    }

    override suspend fun deleteTasksByStateId(stateId: String, taskId: String) {
        try {
            mongoClient.deleteOne(Filters.and(Filters.eq(STATE_ID_FIELD, stateId), (Filters.eq(ID, taskId))))
        } catch (e: Exception) {
            throw DeleteItemFailedException("task delete failed ${e.message}")
        }
    }
}