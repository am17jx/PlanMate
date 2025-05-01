package org.example.data.repository

import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository

class ProjectRepositoryImpl(
    private val localProjectDataSource: LocalProjectDataSource,
) : ProjectRepository {
    override fun createProject(project: Project): Project {
        TODO("Not yet implemented")
    }

    override fun updateProject(updatedProject: Project): Project {
        TODO("Not yet implemented")
    }

    override fun deleteProject(projectId: String) {
        TODO("Not yet implemented")
    }

    override fun getAllProjects(): List<Project> {
        TODO("Not yet implemented")
    }

    override fun getProjectById(projectId: String): Project {
        TODO("Not yet implemented")
    }
}
