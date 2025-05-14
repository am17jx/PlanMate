package org.example.data.repository.sources.remote

import org.example.logic.models.Task
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RemoteTaskDataSource {
    suspend fun createTask(task: Task): Task

    suspend fun updateTask(updatedTask: Task): Task

    suspend fun deleteTask(taskId: Uuid)

    suspend fun getAllTasks(): List<Task>

    suspend fun getTaskById(taskId: Uuid): Task?

    suspend fun getTasksByProjectState(
        stateId: Uuid,
    ): List<Task>
}
