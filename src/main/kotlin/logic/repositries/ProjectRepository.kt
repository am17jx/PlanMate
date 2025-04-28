package org.example.logic.repositries

import org.example.logic.models.Project

interface ProjectRepository {
    fun createProject(project: Project): Project
    fun updateProject(updatedProject: Project): Project
    fun deleteProject(projectId: String)
    fun getAllProjects(): List<Project>
    fun getProjectById(projectId: String): Project
}