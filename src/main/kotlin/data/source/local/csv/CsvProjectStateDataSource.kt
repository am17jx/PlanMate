package org.example.data.source.local.csv

import org.example.data.source.local.contract.LocalProjectStateDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.logic.models.ProjectState
import toCsvLines
import toStates
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CsvProjectStateDataSource(
    private val csvReader: CSVReader,
    private val csvWriter: CSVWriter,
) : LocalProjectStateDataSource {
    private var projectStates = mutableListOf<ProjectState>()

    init {
        readCsvStates()
    }

    override fun createProjectState(projectState: ProjectState): ProjectState {
        projectStates.add(projectState)
        writeCsvStates()
        return projectState
    }

    override fun updateProjectState(updatedTaskProjectState: ProjectState): ProjectState {
        projectStates =
            projectStates
                .map { task ->
                    if (task.id == updatedTaskProjectState.id) {
                        updatedTaskProjectState
                    } else {
                        task
                    }
                }.toMutableList()
        writeCsvStates()
        return updatedTaskProjectState
    }

    override fun deleteProjectState(taskStateId: Uuid) {
        projectStates.removeIf { it.id == taskStateId }
        writeCsvStates()
    }

    override fun getProjectStates(projectId: Uuid): List<ProjectState> {
        return projectStates.filter { it.projectId == projectId }
    }

    override fun getProjectStateById(projectStateId: Uuid): ProjectState? = projectStates.firstOrNull { it.id == projectStateId }

    private fun readCsvStates() {
        csvReader.readLines().toStates().let { updatedStates ->
            projectStates = updatedStates.toMutableList()
        }
    }

    private fun writeCsvStates() {
        csvWriter.writeLines(
            projectStates.toCsvLines(),
        )
        readCsvStates()
    }
}
