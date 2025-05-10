package org.example.logic.useCase.updateProject

import kotlinx.datetime.Clock
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.useCase.GetCurrentUserUseCase
import org.example.logic.utils.BlankInputException
import org.example.logic.utils.ProjectNotChangedException
import org.example.logic.utils.ProjectNotFoundException
import org.example.logic.utils.formattedString
import java.util.*

class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val currentUserUseCase: GetCurrentUserUseCase,

    ) {
    suspend operator fun invoke(updatedProject: Project): Project {
        if (updatedProject.name.isEmpty()) throw BlankInputException(BLANK_PROJECT_NAME_EXCEPTION_MESSAGE)
        val originalProject = currentOriginalProject(updatedProject)
        detectChanges(updatedProject, originalProject)

        return saveUpdatedProject(originalProject, updatedProject, currentUserUseCase())

    }

    private suspend fun saveUpdatedProject(originalProject: Project, newProject: Project, currentUser: User): Project {

        projectRepository.updateProject(newProject)
        return newProject

    }

    private fun detectChanges(originalProject: Project, newProject: Project) {
        if ((originalProject.name == newProject.name) && (originalProject.tasksStatesIds.toSet() == newProject.tasksStatesIds.toSet())) throw ProjectNotChangedException(
            "No changes detected ^_^"
        )
    }

    private suspend fun currentOriginalProject(
        updatedProject: Project
    ): Project {
        return projectRepository.getProjectById(updatedProject.id) ?: throw ProjectNotFoundException(
            PROJECT_NOT_FOUND_EXCEPTION_MESSAGE
        )
    }

    companion object {
        const val PROJECT_NOT_CHANGED_EXCEPTION_MESSAGE = "Project Not changed"
        const val BLANK_PROJECT_NAME_EXCEPTION_MESSAGE = "project name shouldn't be empty"
        const val PROJECT_NOT_FOUND_EXCEPTION_MESSAGE = "Project not found"
    }
}