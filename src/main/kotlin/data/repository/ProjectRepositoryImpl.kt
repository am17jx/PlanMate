package org.example.data.repository

import org.example.data.repository.mapper.mapExceptionsToDomainException
import org.example.data.source.remote.RoleValidationInterceptor
import org.example.data.source.remote.contract.RemoteProjectDataSource
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.utils.NoProjectsFoundException
import org.example.logic.utils.ProjectCreationFailedException
import org.example.logic.utils.ProjectDeletionFailedException
import org.example.logic.utils.ProjectNotChangedException

class ProjectRepositoryImpl(
    private val remoteProjectDataSource: RemoteProjectDataSource,
    private val roleValidationInterceptor: RoleValidationInterceptor
) : ProjectRepository {

    override suspend fun createProject(project: Project) : Project {
        return   mapExceptionsToDomainException(ProjectCreationFailedException()) {
             roleValidationInterceptor.validateRole { remoteProjectDataSource.createProject(project) }
        }
    }


    override suspend fun updateProject(updatedProject: Project): Project {
        return  mapExceptionsToDomainException(ProjectNotChangedException()) {
             roleValidationInterceptor.validateRole { remoteProjectDataSource.updateProject(updatedProject) }
        }
    }

    override suspend fun deleteProject(projectId: String) {
        return  mapExceptionsToDomainException(ProjectDeletionFailedException()) {
             roleValidationInterceptor.validateRole { remoteProjectDataSource.deleteProject(projectId) }
        }
    }


    override suspend fun getAllProjects(): List<Project> {
        return  mapExceptionsToDomainException(NoProjectsFoundException()) {
         remoteProjectDataSource.getAllProjects()
    }
    }

    override suspend fun getProjectById(projectId: String): Project? {
        return mapExceptionsToDomainException(NoProjectsFoundException()) {
             remoteProjectDataSource.getProjectById(projectId)
        }
    }
}
