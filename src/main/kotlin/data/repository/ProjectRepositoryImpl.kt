package org.example.data.repository

import org.example.data.source.remote.RoleValidationInterceptor
import org.example.data.repository.sources.remote.RemoteProjectDataSource
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository

class ProjectRepositoryImpl(
    private val remoteProjectDataSource: RemoteProjectDataSource,
    private val roleValidationInterceptor: RoleValidationInterceptor
) : ProjectRepository {

    override suspend fun createProject(project: Project) : Project {
        return roleValidationInterceptor.validateRole { remoteProjectDataSource.createProject(project) }
    }


    override suspend fun updateProject(updatedProject: Project): Project {
        return roleValidationInterceptor.validateRole {remoteProjectDataSource.updateProject(updatedProject) }
    }

    override suspend fun deleteProject(projectId: String) {
        return roleValidationInterceptor.validateRole { remoteProjectDataSource.deleteProject(projectId) }
    }


    override suspend fun getAllProjects(): List<Project> {
        return remoteProjectDataSource.getAllProjects()
    }

    override suspend fun getProjectById(projectId: String): Project? {
        return remoteProjectDataSource.getProjectById(projectId)
    }
}
