package org.example.logic.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class ProjectState(
    val id: Uuid = Uuid.random(),
    val title: String,
    val projectId: Uuid,
    )
