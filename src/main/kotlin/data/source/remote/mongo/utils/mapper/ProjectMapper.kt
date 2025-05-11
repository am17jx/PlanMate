@file:OptIn(ExperimentalUuidApi::class)

package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.ProjectDTO
import org.example.logic.models.Project
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun ProjectDTO.toProject(): Project {
    return Project(
        id = id,
        name = name,
        projectStateIds = statesIds,
        auditLogsIds = auditLogsIds.map { Uuid.parse(it) }
    )
}

@OptIn(ExperimentalUuidApi::class)
fun Project.toProjectDTO(): ProjectDTO {
    return ProjectDTO(
        id = id,
        name = name,
        statesIds = projectStateIds,
        auditLogsIds = auditLogsIds.map { it.toHexString() }
    )
}