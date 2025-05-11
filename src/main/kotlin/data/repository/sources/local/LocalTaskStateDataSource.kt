package org.example.data.source.local.contract

import org.example.logic.models.ProjectState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface LocalTaskStateDataSource {
    fun createProjectState(projectState: ProjectState): ProjectState
    fun updateProjectState(updatedTaskProjectState: ProjectState): ProjectState
    fun deleteProjectState(taskStateId: Uuid)
    fun getProjectStates(projectId: Uuid): List<ProjectState>
    fun getProjectStateById(projectStateId: Uuid): ProjectState?
}