package org.example.logic.useCase

import org.example.logic.models.Project
import org.example.logic.repositries.ProjectRepository

class GetAllProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(): List<Project> {
        TODO("invoke fun get all project from repo")
    }
}