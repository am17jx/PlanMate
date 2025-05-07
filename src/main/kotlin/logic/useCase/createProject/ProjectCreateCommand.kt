package org.example.logic.useCase.createProject

import org.example.logic.command.Command
import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository

class ProjectCreateCommand(
    private val projectRepository: ProjectRepository,
    private val newProject: Project,
) : Command {

    private var createdProject: Project? = null

    override suspend fun execute() {
        createdProject = projectRepository.createProject(newProject)
    }

    override suspend fun undo() {
        createdProject?.let { projectRepository.deleteProject(newProject.id) }
    }

}