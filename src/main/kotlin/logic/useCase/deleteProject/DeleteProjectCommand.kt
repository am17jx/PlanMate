package org.example.logic.useCase.deleteProject

import org.example.logic.command.Command
import org.example.logic.repositries.ProjectRepository

class DeleteProjectCommand (
    private val projectRepository: ProjectRepository,
    private val projectId: String,
) : Command {
    override fun execute() {
        projectRepository.deleteProject(projectId)
    }

    override fun undo() {
        projectRepository.createProject(projectRepository.getProjectById(projectId)!!)
    }
}