@file:OptIn(ExperimentalUuidApi::class)

package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.TaskDTO
import org.example.logic.models.Task
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


fun TaskDTO.toTask(): Task {
    return Task(
        id = id,
        name = name,
        stateId = stateId,
        stateName = stateName,
        addedBy = addedBy,
        auditLogsIds = auditLogsIds.map { Uuid.parse(it) },
        projectId = projectId
    )
}

fun Task.toTaskDTO(): TaskDTO {
    return TaskDTO(
        id = id,
        name = name,
        stateId = stateId,
        stateName = stateName,
        addedBy = addedBy,
        auditLogsIds = auditLogsIds.map { it.toHexString() },
        projectId = projectId
    )

}
