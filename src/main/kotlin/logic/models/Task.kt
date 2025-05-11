package org.example.logic.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Task(
    val id: Uuid = Uuid.random(),
    val name: String,
    val stateId: Uuid,
    val stateName: String,
    val addedById: Uuid,
    val addedByName: String,
    val projectId: Uuid
)
