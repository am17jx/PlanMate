package org.example.data.utils.mapper

import org.example.logic.models.Project

fun Project.toCsvLine(): String {
    TODO("Not yet implemented")
}

fun String.toProject(): Project {
    TODO("Not yet implemented")
}

fun List<Project>.toCsvLines(): List<String> = this.map { it.toCsvLine() }

fun List<String>.toProjectList(): List<Project> = this.map { it.toProject() }
