package org.example.data.source.local.csv

import org.example.data.repository.sources.local.LocalProjectDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.data.source.local.csv.utils.mapper.toCsvLines
import org.example.data.source.local.csv.utils.mapper.toProjectList
import org.example.logic.models.Project
import org.example.logic.utils.ProjectCreationFailedException
import org.example.logic.utils.ProjectNotChangedException
import java.io.IOException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
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
            throw ProjectCreationFailedException()
        }

    override fun updateProject(updatedProject: Project): Project =
        try {
            projects.removeIf { it.id == updatedProject.id }
            projects.add(updatedProject)
            saveToFile()
            updatedProject
        } catch (_: IOException) {
            throw ProjectNotChangedException()
        }

    override fun deleteProject(projectId: Uuid) {
        projects.removeIf { it.id == projectId }
        saveToFile()
    }

    override fun getAllProjects(): List<Project> = projects

    override fun getProjectById(projectId: Uuid): Project? = projects.find { it.id == projectId }

    private fun saveToFile() {
        csvWriter.writeLines(projects.toCsvLines())
    }

    private fun loadFromFile(): List<Project> = csvReader.readLines().toProjectList()
}
