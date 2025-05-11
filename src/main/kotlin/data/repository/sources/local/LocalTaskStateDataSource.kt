package org.example.data.source.local.contract

import org.example.logic.models.State
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface LocalTaskStateDataSource {
    fun createTaskState(state: State): State

    fun updateTaskState(updatedTaskState: State): State

    fun deleteTaskState(taskStateId: Uuid)

    fun getAllTaskStates(): List<State>

    fun getTaskStateById(taskStateId: Uuid): State?
}
