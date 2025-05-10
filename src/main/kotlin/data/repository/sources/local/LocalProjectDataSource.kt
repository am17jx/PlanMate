package org.example.data.repository.sources.local

import org.example.logic.models.Project
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface LocalProjectDataSource {
    fun createProject(project: Project): Project

    fun updateProject(updatedProject: Project): Project

    fun deleteProject(projectId: Uuid)

    fun getAllProjects(): List<Project>

    fun getProjectById(projectId: Uuid): Project?
}
