package org.example.data.utils.mapper

import org.example.logic.models.Task

typealias CsvLine = String

fun List<CsvLine>.toTasks(): List<Task> {
    return emptyList()
}

fun List<Task>.toCsvLines(): List<CsvLine> {
    return emptyList()
}
