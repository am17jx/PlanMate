package org.example.data.source.remote.models


data class TaskDTO(
    val id: String,
    val name: String,
    val stateId: String,
    val stateName: String,
    val addedBy: String,
    val auditLogsIds: List<String>,
    val projectId: String
)