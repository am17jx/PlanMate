package org.example.logic.command

import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository


class UpdateProjectCommand(
    private val projectRepository: ProjectRepository,
    private val newProject: Project,
    private val originalProject: Project
) : Command {

    private var updatedProject: Project? = null

    override fun execute() {
        updatedProject = projectRepository.updateProject(newProject)
    }

    override fun undo() {
        updatedProject?.let { projectRepository.updateProject(originalProject) }
    }

}