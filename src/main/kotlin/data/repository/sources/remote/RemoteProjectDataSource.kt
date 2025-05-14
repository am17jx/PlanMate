package org.example.data.repository.sources.remote

import org.example.logic.models.Project
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface RemoteProjectDataSource {
    suspend fun createProject(project: Project): Project

    suspend fun updateProject(updatedProject: Project): Project

    suspend fun deleteProject(projectId: Uuid)

    suspend fun getAllProjects(): List<Project>

    suspend fun getProjectById(projectId: Uuid): Project?
}
