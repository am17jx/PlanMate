package org.example.data.models

import org.example.logic.models.State

data class ProjectDTO(
    val id: String,
    val name: String,
    val states: List<State>,
    val auditLogsIds: List<String>
)

