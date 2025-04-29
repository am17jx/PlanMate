package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository

class GetProjectByIdUseCase(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(projectId: String): Project {
        TODO("invoke fun get project by id from repo")
    }


}