package org.example.data.utils.mapper

import org.example.logic.models.Task

typealias CsvLine = String

fun List<CsvLine>.toTasks(): List<Task> {
    if (this.size <= 1) return emptyList()

    return this.drop(1)
        .filter { it.isNotBlank() }
        .map { line ->
            line
                .split(",")
                .map { it.trim() }
                .takeIf { it.size == 6 }
                ?.let { parts ->
                    Task(
                        id = parts[0],
                        name = parts[1],
                        stateId = parts[2],
                        addedBy = parts[3],
                        auditLogsIds = if (parts[4].isBlank()) emptyList() else parts[4].split("|"),
                        projectId = parts[5]
                    )
                } ?: throw IllegalArgumentException("CSV line doesn't have enough fields: $line")
        }
}

fun List<Task>.toCsvLines(): List<CsvLine> {
    val header = "id,name,stateId,addedBy,auditLogsIds,projectId"
    val dataLines = this.map { task ->
        if(task.name.contains(",")) throw IllegalArgumentException("CSV fields cannot contain comma")
        listOf(
            task.id,
            task.name,
            task.stateId,
            task.addedBy,
            task.auditLogsIds.joinToString("|"),
            task.projectId
        ).joinToString(",")
    }

    return listOf(header) + dataLines
}