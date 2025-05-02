package org.example.data.source.local

import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.example.data.utils.mapper.toCsvLines
import org.example.data.utils.mapper.toProjectList
import org.example.logic.models.Project
import org.example.logic.utils.ProjectCreationFailedException
import org.example.logic.utils.ProjectNotChangedException
import java.io.IOException

class CsvProjectDataSource(
    private val csvReader: CSVReader,
    private val csvWriter: CSVWriter,
) : LocalProjectDataSource {
    private val projects = mutableListOf<Project>()

    init {
        projects.addAll(loadFromFile())
    }

    override fun createProject(project: Project): Project =
        try {
            projects.add(project)
            saveToFile()
            project
        } catch (_: IOException) {
            throw ProjectCreationFailedException("PROJECT_CREATION_FAILED_EXCEPTION_MESSAGE")
        }

    override fun updateProject(updatedProject: Project): Project =
        try {
            projects.removeIf { it.id == updatedProject.id }
            projects.add(updatedProject)
            saveToFile()
            updatedProject
        } catch (_: IOException) {
            throw ProjectNotChangedException("Project Not changed")
        }

    override fun deleteProject(projectId: String) {
        projects.removeIf { it.id.contains(projectId) }
        saveToFile()
    }

    override fun getAllProjects(): List<Project> = projects

    override fun getProjectById(projectId: String): Project? =
        projects.find { it.id == projectId }

    private fun saveToFile() {
        csvWriter.writeLines(projects.toCsvLines())
    }

    private fun loadFromFile(): List<Project> = csvReader.readLines().toProjectList()
}
