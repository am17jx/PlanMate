package org.example.data.source.local.contract

import org.example.logic.models.ProjectState
interface LocalTaskStateDataSource {
    fun createTaskState(projectState: ProjectState): ProjectState
    fun updateTaskState(updatedTaskProjectState: ProjectState): ProjectState
    fun deleteTaskState(taskStateId: String)
    fun getAllTaskStates(): List<ProjectState>
    fun getTaskStateById(taskStateId: String): ProjectState?
}