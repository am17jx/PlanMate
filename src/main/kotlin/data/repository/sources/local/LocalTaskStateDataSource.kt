package org.example.data.source.local.contract

import org.example.logic.models.ProjectState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface LocalTaskStateDataSource {
    fun createTaskState(projectState: ProjectState): ProjectState
    fun updateTaskState(updatedTaskProjectState: ProjectState): ProjectState
    fun deleteTaskState(taskStateId: Uuid)
    fun getAllTaskStates(): List<ProjectState>
    fun getTaskStateById(taskStateId: Uuid): ProjectState?
}