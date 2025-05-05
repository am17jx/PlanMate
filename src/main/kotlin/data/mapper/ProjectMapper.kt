package org.example.data.mapper

import org.example.data.models.ProjectDTO
import org.example.logic.models.Project

fun ProjectDTO.toProject(): Project {
    return Project(
        id = id,
        name = name,
        states = states,
        auditLogsIds = auditLogsIds
    )
}

fun Project.toProjectDTO(): ProjectDTO {
    return ProjectDTO(
        id = id,
        name = name,
        states = states,
        auditLogsIds = auditLogsIds
    )

}