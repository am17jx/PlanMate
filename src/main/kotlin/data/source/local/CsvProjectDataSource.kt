package org.example.data.source.local

import org.example.data.source.local.contract.LocalProjectDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.example.data.utils.mapper.toCsvLines
import org.example.data.utils.mapper.toProjectList
import org.example.logic.models.Project
import org.example.logic.utils.ProjectNotChangedException
import org.example.logic.utils.ProjectNotFoundException
import java.io.IOException

class CsvProjectDataSource(
    private val csvReader: CSVReader,
    private val csvWriter: CSVWriter,
) : LocalProjectDataSource {
    override fun createProject(project: Project): Project {
        TODO("Not yet implemented")
    }

    override fun updateProject(updatedProject: Project): Project {
        TODO("Not yet implemented")
    }

    override fun deleteProject(projectId: String) {
        TODO("Not yet implemented")
    }

    override fun getAllProjects(): List<Project> {
        TODO("Not yet implemented")
    }

    override fun getProjectById(projectId: String): Project {
        TODO("Not yet implemented")
    }
}
