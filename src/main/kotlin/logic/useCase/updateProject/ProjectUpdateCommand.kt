package org.example.logic.useCase.updateProject

import org.example.logic.command.Command
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository


class ProjectUpdateCommand(
    private val projectRepository: ProjectRepository,
    private val newProject: Project,
    private val originalProject: Project
) : Command {

    private var updatedProject: Project? = null

    override suspend fun execute() {
        updatedProject = projectRepository.updateProject(newProject)
    }

    override suspend fun undo() {
        updatedProject?.let { projectRepository.updateProject(originalProject) }
    }

}