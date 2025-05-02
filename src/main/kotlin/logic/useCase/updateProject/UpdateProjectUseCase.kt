package org.example.logic.useCase.updateProject

import kotlinx.datetime.Clock
import org.example.logic.command.Command
import org.example.logic.command.CreateAuditLogCommand
import org.example.logic.command.TransactionalCommand
import org.example.logic.models.*
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.useCase.deleteTask.DeleteTaskCommand
import org.example.logic.utils.*
import java.util.*

class UpdateProjectUseCase(
    private val projectRepository: ProjectRepository,
    private val auditLogRepository: AuditLogRepository,
    private val authenticationRepository: AuthenticationRepository,
    private val getProjectTasksUseCase: GetProjectTasksUseCase,
    private val taskRepository: TaskRepository

) {
    operator fun invoke(updatedProject: Project): Project {
        if (updatedProject.name.isEmpty()) throw BlankInputException(BLANK_PROJECT_NAME_EXCEPTION_MESSAGE)
        val originalProject = currentOriginalProject(updatedProject)
        val currentUser = currentUser()
        if (currentUser.role != UserRole.ADMIN) throw UnauthorizedException(UNAUTHORIZED_EXCEPTION_MESSAGE)
        detectChanges(updatedProject, originalProject)

        return saveUpdatedProject(originalProject, updatedProject, currentUser)

    }

    private fun saveUpdatedProject(originalProject: Project, newProject: Project, currentUser: User): Project {
        val actionBuilder = actionBuilder(originalProject, newProject, currentUser)
        val auditLog = createAuditLogInstance(originalProject, newProject, currentUser, actionBuilder.first)

        val auditCommand = CreateAuditLogCommand(auditLogRepository, auditLog)
        val projectUpdateCommand = ProjectUpdateCommand(projectRepository, newProject, originalProject)

        val tasksCommand: MutableList<Command> = mutableListOf()
        if(actionBuilder.second.isNotEmpty())
        getProjectTasksUseCase(newProject.id).filter { it.stateId == actionBuilder.second}.forEach {
            tasksCommand.add(DeleteTaskCommand(taskRepository, it))
        }

        tasksCommand.add(projectUpdateCommand)
        tasksCommand.add(auditCommand)
        val updateProjectCommandTransaction = TransactionalCommand(
           tasksCommand,
            ProjectNotChangedException(PROJECT_NOT_CHANGED_EXCEPTION_MESSAGE)
        )

        try {
            updateProjectCommandTransaction.execute()
        } catch (e: ProjectNotChangedException) {
            throw e
        }

        return newProject.copy(auditLogsIds = newProject.auditLogsIds.plus(auditCommand.getCreatedLog()?.id ?: ""))

    }

    private fun createAuditLogInstance(
        project: Project,
        updatedProject: Project,
        currentUser: User,
        action: String
    ): AuditLog {

        val auditLog = AuditLog(
            id = UUID.randomUUID().toString(),
            userId = currentUser.id,
            action = action,
            timestamp = System.currentTimeMillis(),
            entityType = AuditLogEntityType.PROJECT,
            entityId = project.id,
            actionType = AuditLogActionType.UPDATE
        )
        return auditLog
    }

    private fun actionBuilder(project: Project, updatedProject: Project, currentUser: User): Pair<String, String> {
        var deletedStateId = ""
        val timestampNow = Clock.System.now()
        val action = when {
            project.name != updatedProject.name ->
                "${currentUser.username} changed Project name from ${project.name} to ${updatedProject.name}"

            project.states.size > updatedProject.states.size -> {
                deletedStateId = project.states.subtract(updatedProject.states.toSet()).first().id
                "${currentUser.username} deleted a state: ${
                    project.states.subtract(updatedProject.states.toSet()).first().title
                }, from ${project.name}"
            }

            project.states.size < updatedProject.states.size ->
                "${currentUser.username} add  state: ${
                    updatedProject.states.subtract(project.states.toSet()).first().title
                }, to ${project.name}"

            else -> {
                val statesDifference = (updatedProject.states.subtract(project.states.toSet()) + project.states.subtract(updatedProject.states.toSet())).toList()
                "${currentUser.username} updated state : ${statesDifference[1].title} to state:${statesDifference[0].title}, in ${project.name}"
            }

        }
        return Pair(action + "at ${timestampNow.formattedString()}",deletedStateId)
    }

    private fun currentUser(): User {
        return authenticationRepository.getCurrentUser()
            ?: throw NoLoggedInUserException("No logged-in user found")
    }

    private fun detectChanges(originalProject: Project, newProject: Project) {
        if ((originalProject.name == newProject.name) && (originalProject.states.toSet()==newProject.states.toSet()))
            throw ProjectNotChangedException("No changes detected ^_^")
    }

    private fun currentOriginalProject(
        updatedProject: Project
    ): Project {
        return projectRepository.getProjectById(updatedProject.id)
            ?: throw ProjectNotFoundException(PROJECT_NOT_FOUND_EXCEPTION_MESSAGE)
    }

    companion object {
        const val PROJECT_NOT_CHANGED_EXCEPTION_MESSAGE = "Project Not changed"
        const val BLANK_PROJECT_NAME_EXCEPTION_MESSAGE = "project name shouldn't be empty"
        const val UNAUTHORIZED_EXCEPTION_MESSAGE = "Only admins can update projects."
        const val PROJECT_NOT_FOUND_EXCEPTION_MESSAGE = "Project not found"
        const val NOT_LOGGED_IN_USER_EXCEPTION_MESSAGE = "No logged-in user found"
    }
}