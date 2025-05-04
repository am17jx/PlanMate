package org.example.data.source.local.csv.utils.mapper

import org.example.data.utils.Constants.ProjectParsingKeys.AUDIT_LOGS_IDS_INDEX
import org.example.data.utils.Constants.ProjectParsingKeys.ID_INDEX
import org.example.data.utils.Constants.ProjectParsingKeys.NAME_INDEX
import org.example.data.utils.Constants.ProjectParsingKeys.STATES_INDEX
import org.example.logic.models.Project
import org.example.logic.models.State

fun Project.toCsvLine(): String =
    "$id," +
        "$name," +
        "[${states.joinToString(",") { it.id + ":" + it.title }}]," +
        "[${auditLogsIds.joinToString(",")}]"

fun String.toProject(): Project {
    val segments = this.parsedSegments()

    if (segments.size < 4) throw IllegalArgumentException("Input string doesn't have enough segments: $this")

    return Project(
        id = segments[ID_INDEX],
        name = segments[NAME_INDEX],
        states =
            segments[STATES_INDEX]
                .trim('[', ']')
                .takeIf { it.isNotBlank() }
                ?.split(",")
                ?.map { stateStr ->
                    val parts = stateStr.split(":")
                    if (parts.size < 2) throw IllegalArgumentException("Invalid state format: $stateStr")
                    State(parts[0], parts[1])
                } ?: emptyList(),
        auditLogsIds =
            segments[AUDIT_LOGS_IDS_INDEX]
                .trim('[', ']')
                .takeIf { it.isNotBlank() }
                ?.split(",")
                ?.map { it.trim() }
                ?: emptyList(),
    )
}

private fun String.parsedSegments(): List<String> {
    if (this.isBlank()) return emptyList()

    val segments = mutableListOf<String>()
    var currentSegment = StringBuilder()
    var insideBrackets = 0

    for (char in this) {
        when (char) {
            '[' -> {
                insideBrackets++
                currentSegment.append(char)
            }

            ']' -> {
                insideBrackets--
                currentSegment.append(char)
            }

            ',' -> {
                if (insideBrackets > 0) {
                    currentSegment.append(char)
                } else {
                    segments.add(currentSegment.toString().trim())
                    currentSegment = StringBuilder()
                }
            }

            else -> currentSegment.append(char)
        }
    }

    if (currentSegment.isNotEmpty()) {
        segments.add(currentSegment.toString().trim())
    }

    return segments
}

fun List<Project>.toCsvLines(): List<String> = this.map { it.toCsvLine() }

fun List<String>.toProjectList(): List<Project> = this.map { it.toProject() }
