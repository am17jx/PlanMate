package org.example.data.source.local.csv

import org.example.data.repository.sources.local.LocalTaskDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.data.source.local.csv.utils.mapper.toCsvLines
import org.example.data.source.local.csv.utils.mapper.toTasks
import org.example.logic.models.Task
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CsvTaskDataSource(
    private val csvReader: CSVReader,
    private val csvWriter: CSVWriter,
) : LocalTaskDataSource {
    private var tasks = mutableListOf<Task>()

    init {
        readCsvTasks()
    }

    override fun createTask(task: Task): Task {
        tasks.add(task)
        writeCsvTasks()
        return task
    }

    override fun updateTask(updatedTask: Task): Task {
        tasks =
            tasks
                .map { task ->
                    if (task.id == updatedTask.id) {
                        updatedTask
                    } else {
                        task
                    }
                }.toMutableList()
        writeCsvTasks()
        return updatedTask
    }

    override fun deleteTask(taskId: Uuid) {
        tasks.removeIf { it.id == taskId }
        writeCsvTasks()
    }

    override fun getAllTasks(): List<Task> = tasks

    override fun getTaskById(taskId: Uuid): Task? = tasks.firstOrNull { it.id == taskId }
    override suspend fun getTasksByProjectState(stateId: Uuid): List<Task> {
        return tasks.filter { it.stateId == stateId }
    }

    private fun readCsvTasks() {
        csvReader.readLines().toTasks().let { updatedTasks ->
            tasks = updatedTasks.toMutableList()
        }
    }

    private fun writeCsvTasks() {
        csvWriter.writeLines(
            tasks.toCsvLines(),
        )
        readCsvTasks()
    }
}
