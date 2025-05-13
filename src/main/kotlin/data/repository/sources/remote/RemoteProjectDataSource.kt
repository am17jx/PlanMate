package org.example.data.repository.sources.remote

import org.example.logic.models.Project

interface RemoteProjectDataSource {
    suspend fun createProject(project: Project): Project

    suspend fun updateProject(updatedProject: Project): Project

    suspend fun deleteProject(projectId: String)

    suspend fun getAllProjects(): List<Project>

    suspend fun getProjectById(projectId: String): Project?
}
