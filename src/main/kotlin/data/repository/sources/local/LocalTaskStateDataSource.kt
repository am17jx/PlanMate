package org.example.data.source.local.contract

import org.example.logic.models.State
interface LocalTaskStateDataSource {
    fun createTaskState(state: State): State
    fun updateTaskState(updatedTaskState: State): State
    fun deleteTaskState(taskStateId: String)
    fun getAllTaskStates(): List<State>
    fun getTaskStateById(taskStateId: String): State?
}