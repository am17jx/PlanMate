package org.example.data.source.local.contract

import org.example.logic.models.Project

interface LocalProjectDataSource {
    fun createProject(project: Project): Project

    fun updateProject(updatedProject: Project): Project

    fun deleteProject(projectId: String)

    fun getAllProjects(): List<Project>

    fun getProjectById(projectId: String): Project?
}
