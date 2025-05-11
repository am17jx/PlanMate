@file:OptIn(ExperimentalUuidApi::class)

package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.ProjectDTO
import org.example.logic.models.Project
import org.example.logic.utils.toUuid
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun ProjectDTO.toProject(): Project =
    Project(
        id = id.toUuid(),
        name = name
    )

@OptIn(ExperimentalUuidApi::class)
fun Project.toProjectDTO(): ProjectDTO =
    ProjectDTO(
        id = id.toHexString(),
        name = name,
    )
