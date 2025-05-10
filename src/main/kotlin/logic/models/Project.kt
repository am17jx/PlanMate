package org.example.logic.models

data class Project(
    val id: String,
    val name: String,
    val tasksStatesIds: List<String>,
    val auditLogsIds: List<String>
)
