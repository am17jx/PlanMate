package org.example.data.source.local.csv

import org.example.data.source.local.contract.LocalTaskStateDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.logic.models.ProjectState
import toCsvLines
import toStates
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CsvTaskStateDataSource(
    private val csvReader: CSVReader,
    private val csvWriter: CSVWriter,
) : LocalTaskStateDataSource {
    private var projectStates = mutableListOf<ProjectState>()

    init {
        readCsvStates()
    }

    override fun createTaskState(projectState: ProjectState): ProjectState {
        projectStates.add(projectState)
        writeCsvStates()
        return projectState
    }

    override fun updateTaskState(updatedTaskProjectState: ProjectState): ProjectState {
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

    override fun deleteTaskState(taskStateId: String) {
        projectStates.removeIf { it.id.toHexString() == taskStateId }
        writeCsvStates()
    }

    override fun getAllTaskStates(): List<ProjectState> = projectStates

    override fun getTaskStateById(taskStateId: String): ProjectState? = projectStates.firstOrNull { it.id.toHexString() == taskStateId }

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
