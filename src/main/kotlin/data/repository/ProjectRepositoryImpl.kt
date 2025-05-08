package org.example.data.repository

import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.data.source.remote.contract.RemoteProjectDataSource
import org.example.logic.models.AuditLog
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository

class ProjectRepositoryImpl(
    private val remoteProjectDataSource: RemoteProjectDataSource,
) : ProjectRepository {
    override suspend fun createProject(project: Project): Project =
        remoteProjectDataSource.createProject(project)

    override suspend fun updateProject(updatedProject: Project): Project =
        remoteProjectDataSource.updateProject(updatedProject)

    override suspend fun deleteProject(projectId: String) {
        remoteProjectDataSource.deleteProject(projectId)
    }

    override suspend fun getAllProjects(): List<Project> =
        remoteProjectDataSource.getAllProjects()

    override suspend fun getProjectById(projectId: String): Project? =
        remoteProjectDataSource.getProjectById(projectId)
}
