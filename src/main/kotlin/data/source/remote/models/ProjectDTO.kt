package org.example.data.source.remote.models

data class ProjectDTO(
    val id: String,
    val name: String,
    val statesIds: List<String>,
    val auditLogsIds: List<String>
)

