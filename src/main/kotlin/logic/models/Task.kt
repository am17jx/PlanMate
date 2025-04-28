package org.example.logic.models

data class Task(
    val id: String,
    val name: String,
    val stateId: String,
    val addedBy: String,
    val auditLogsIds: List<String>,
    val projectId: String
)
