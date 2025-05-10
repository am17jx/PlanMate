package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.models.State
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.getCroppedId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CreateStateUseCase(
    private val projectRepository: ProjectRepository,
    private val updateProjectUseCase: UpdateProjectUseCase,
) {
    suspend operator fun invoke(
        projectId: String,
        stateName: String,
    ): Project {
        checkInputValidation(stateName, projectId)
        val project = getProject(projectId)
        val newState =
            State(
                id = Uuid.random().getCroppedId(),
                title = stateName,
            )
        val updatedProject =
            updateProjectUseCase(
                project.copy(
                    states = project.states + newState,
                ),
            )
        return updatedProject
    }

    private suspend fun getProject(projectId: String): Project =
        (
            projectRepository.getProjectById(projectId)
                ?: throw ProjectNotFoundException()
        )


    private fun checkInputValidation(
        stateName: String,
        projectId: String,
    ) {
        when {
            stateName.isBlank() -> throw BlankInputException()
            projectId.isBlank() -> throw BlankInputException()
        }
    }

}
