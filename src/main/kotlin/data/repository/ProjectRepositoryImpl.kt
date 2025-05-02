package org.example.data.repository

import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository

class ProjectRepositoryImpl(
    private val localProjectDataSource: LocalProjectDataSource,
) : ProjectRepository {
    override fun createProject(project: Project): Project =
        localProjectDataSource.createProject(project)

    override fun updateProject(updatedProject: Project): Project =
        localProjectDataSource.updateProject(updatedProject)

    override fun deleteProject(projectId: String) {
        localProjectDataSource.deleteProject(projectId)
    }

    override fun getAllProjects(): List<Project> =
        localProjectDataSource.getAllProjects()

    override fun getProjectById(projectId: String): Project? =
        localProjectDataSource.getProjectById(projectId)
}
