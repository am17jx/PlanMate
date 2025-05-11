package org.example.data.source.remote.contract

import org.example.logic.models.ProjectState

interface RemoteTaskStateDataSource {
    suspend fun createTaskState(taskProjectState: ProjectState): ProjectState

    suspend fun updateTaskState(updatedTaskProjectState: ProjectState): ProjectState

    suspend fun deleteTaskState(taskStateId: String)

    suspend fun getProjectTaskStates(projectId: String): List<ProjectState>

    suspend fun getTaskStateById(taskStateId: String): ProjectState?
}