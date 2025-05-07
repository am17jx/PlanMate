package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.InvalidInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.isValidId

class GetProjectByIdUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(projectId: String): Project {
        return projectId
            .takeIf {validateProjectId(it) }
            .let { projectRepository.getProjectById(projectId) ?: throw ProjectNotFoundException("project not found") }
    }


    private fun validateProjectId(projectId: String): Boolean{
        require(projectId.isNotBlank()) { throw BlankInputException("Project ID is blank") }
        require(projectId.isValidId()) { throw InvalidInputException("Project ID is invalid") }
        return true
    }

}