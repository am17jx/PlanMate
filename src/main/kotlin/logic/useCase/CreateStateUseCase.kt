package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository

class CreateStateUseCase(
    private val projectRepository: ProjectRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val updateProjectUseCase: UpdateProjectUseCase,
) {
    operator fun invoke(
        stateName: String,
        projectId: String,
    ): Project {
        TODO("Not yet implemented")
    }
}
