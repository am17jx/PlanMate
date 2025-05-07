package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.TaskDTO
import org.example.logic.models.Task


fun TaskDTO.toTask(): Task {
    return Task(
        id = id,
        name = name,
        stateId = stateId,
        addedBy = addedBy,
        auditLogsIds = auditLogsIds,
        projectId = projectId
    )
}

fun Task.toTaskDTO(): TaskDTO {
    return TaskDTO(
        id = id,
        name = name,
        stateId = stateId,
        addedBy = addedBy,
        auditLogsIds = auditLogsIds,
        projectId = projectId
    )

}
