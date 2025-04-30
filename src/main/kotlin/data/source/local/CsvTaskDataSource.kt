package org.example.data.source.local

import org.example.data.source.local.contract.LocalTaskDataSource
import org.example.data.utils.CSVReader
import org.example.data.utils.CSVWriter
import org.example.data.utils.mapper.toCsvLines
import org.example.data.utils.mapper.toTasks
import org.example.logic.models.Task

class CsvTaskDataSource(
    private val csvReader: CSVReader,
    private val csvWriter: CSVWriter
): LocalTaskDataSource {

    override fun createTask(task: Task): Task {
        TODO("Not yet implemented")
    }

    override fun updateTask(updatedTask: Task): Task {
        TODO("Not yet implemented")
    }

    override fun deleteTask(taskId: String) {
        TODO("Not yet implemented")
    }

    override fun getAllTasks(): List<Task> {
        TODO("Not yet implemented")
    }

    override fun getTaskById(taskId: String): Task? {
        TODO("Not yet implemented")
    }
}