package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.StateDTO
import org.example.logic.models.ProjectState
import org.example.logic.utils.toUuid
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun StateDTO.toState(): ProjectState = ProjectState(id = id.toUuid(), title = title, projectId = projectId.toUuid())

@OptIn(ExperimentalUuidApi::class)
fun ProjectState.toStateDTO(): StateDTO = StateDTO(id = id.toHexString(), title = title, projectId = projectId.toHexString())
