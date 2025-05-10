package org.example.logic.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Project(
    val id: String,
    val name: String,
    val tasksStatesIds: List<String>,
    val auditLogsIds: List<Uuid>
)
