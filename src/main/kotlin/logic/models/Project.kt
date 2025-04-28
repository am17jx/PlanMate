package org.example.logic.models

data class Project(
    val id: String,
    val name: String,
    val states: List<State>,
    val auditLogsIds: List<String>
)
