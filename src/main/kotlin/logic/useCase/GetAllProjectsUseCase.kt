package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.utils.NoProjectsFoundException

class GetAllProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(): List<Project> {
       return  projectRepository.getAllProjects()
           .takeIf { it.isNotEmpty() }
           ?: throw NoProjectsFoundException("No projects found")

    }
}