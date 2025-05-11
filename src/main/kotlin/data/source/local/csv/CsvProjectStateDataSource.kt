package org.example.data.source.local.csv

import org.example.data.source.local.contract.LocalTaskStateDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.logic.models.State
import toCsvLines
import toStates
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CsvTaskStateDataSource(
    private val csvReader: CSVReader,
    private val csvWriter: CSVWriter,
) : LocalTaskStateDataSource {
    private var states = mutableListOf<State>()

    init {
        readCsvStates()
    }

    override fun createTaskState(state: State): State {
        states.add(state)
        writeCsvStates()
        return state
    }

    override fun updateTaskState(updatedTaskState: State): State {
        states =
            states
                .map { task ->
                    if (task.id == updatedTaskState.id) {
                        updatedTaskState
                    } else {
                        task
                    }
                }.toMutableList()
        writeCsvStates()
        return updatedTaskState
    }

    override fun deleteTaskState(taskStateId: Uuid) {
        states.removeIf { it.id == taskStateId }
        writeCsvStates()
    }

    override fun getAllTaskStates(): List<State> = states

    override fun getTaskStateById(taskStateId: Uuid): State? = states.firstOrNull { it.id == taskStateId }

    private fun readCsvStates() {
        csvReader.readLines().toStates().let { updatedStates ->
            states = updatedStates.toMutableList()
        }
    }

    private fun writeCsvStates() {
        csvWriter.writeLines(
            states.toCsvLines(),
        )
        readCsvStates()
    }
}
