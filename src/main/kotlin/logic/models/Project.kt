package org.example.logic.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Project(
    val id: String,
    val name: String,
    val projectStateIds: List<String>,
    val auditLogsIds: List<Uuid>
)
