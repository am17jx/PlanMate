package org.example.data.source.local

import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.example.data.utils.mapper.toCsvLines
import org.example.data.utils.mapper.toTasks
import org.example.logic.models.Task

class CsvTaskDataSource(
    private val csvReader: CSVReader, private val csvWriter: CSVWriter
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
        tasks = tasks.map { task ->
            if (task.id == updatedTask.id) {
                updatedTask
            } else {
                task
            }
        }.toMutableList()
        writeCsvTasks()
        return updatedTask
    }

    override fun deleteTask(taskId: String) {
        tasks.removeIf { it.id == taskId }
        writeCsvTasks()
    }

    override fun getAllTasks(): List<Task> {
        return tasks
    }

    override fun getTaskById(taskId: String): Task? {
        return tasks.firstOrNull { it.id == taskId }
    }

    override fun deleteTasksByStateId(stateId: String, projectId: String) {
        tasks.removeIf { it.stateId == stateId && it.projectId == projectId }
        writeCsvTasks()
    }


    private fun readCsvTasks() {
        csvReader.readLines().toTasks().let { updatedTasks ->
            tasks = updatedTasks.toMutableList()
        }
    }

    private fun writeCsvTasks() {
        csvWriter.writeLines(
            tasks.toCsvLines()
        )
        readCsvTasks()
    }
}