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

    private suspend fun saveUpdatedProject(
        originalProject: Project,
        newProject: Project,
        currentUser: User,
    ): Project {
        val actionBuilder = actionBuilder(originalProject, newProject, currentUser)
        val auditLog = createAuditLogInstance(originalProject, currentUser, actionBuilder.first)

        auditLogRepository.createAuditLog(auditLog)

        projectRepository.updateProject(newProject)
        return newProject
    }

    private fun createAuditLogInstance(
        project: Project,
        currentUser: User,
        action: String,
    ): AuditLog {
        val currentTime = Clock.System.now()
        val auditLog =
            AuditLog(
                id = UUID.randomUUID().toString(),
                userId = currentUser.id,
                action = action,
                createdAt = currentTime,
                entityType = AuditLogEntityType.PROJECT,
                entityId = project.id,
                actionType = AuditLogActionType.UPDATE,
            )
        return auditLog
    }

    private fun actionBuilder(
        project: Project,
        updatedProject: Project,
        currentUser: User,
    ): Pair<String, String> {
        var deletedStateId = ""
        val currentTime = Clock.System.now()
        val action =
            when {
                project.name != updatedProject.name -> "${currentUser.username} changed Project name from ${project.name} to ${updatedProject.name}"

                project.states.size > updatedProject.states.size -> {
                    deletedStateId =
                        project.states
                            .subtract(updatedProject.states.toSet())
                            .first()
                            .id
                    "${currentUser.username} deleted a state: ${
                        project.states.subtract(updatedProject.states.toSet()).first().title
                    } from ${project.name}"
                }

                project.states.size < updatedProject.states.size ->
                    "${currentUser.username} add  state: ${
                        updatedProject.states.subtract(project.states.toSet()).first().title
                    } to ${project.name}"

                else -> {
                    val statesDifference =
                        (
                            updatedProject.states.subtract(
                                project.states.toSet(),
                            ) + project.states.subtract(updatedProject.states.toSet())
                        ).toList()
                    "${currentUser.username} updated state : ${statesDifference[1].title} to state:${statesDifference[0].title} in ${project.name}"
                }
            }
        return Pair(action + "at ${currentTime.formattedString()}", deletedStateId)
    }

    private fun detectChanges(
        originalProject: Project,
        newProject: Project,
    ) {
        if ((originalProject.name == newProject.name) &&
            (originalProject.states.toSet() == newProject.states.toSet())
        ) {
            throw ProjectNotChangedException(
                "No changes detected ^_^",
            )
        }
    }

    private suspend fun currentOriginalProject(updatedProject: Project): Project =
        projectRepository.getProjectById(updatedProject.id) ?: throw ProjectNotFoundException(
            PROJECT_NOT_FOUND_EXCEPTION_MESSAGE,
        )

    companion object {
        const val PROJECT_NOT_CHANGED_EXCEPTION_MESSAGE = "Project Not changed"
        const val BLANK_PROJECT_NAME_EXCEPTION_MESSAGE = "project name shouldn't be empty"
        const val PROJECT_NOT_FOUND_EXCEPTION_MESSAGE = "Project not found"
    }
}
