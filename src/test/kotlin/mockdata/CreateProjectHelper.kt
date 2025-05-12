@file:OptIn(ExperimentalUuidApi::class)

package mockdata

import org.example.logic.models.Project
import org.example.logic.models.ProjectState
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun createProject(
    id: Uuid = Uuid.random(),
    name: String = ""
) = Project(
    id = id,
    name = name,
)

fun createState(
    id: Uuid = Uuid.random(),
    title: String = "",
    projectId: Uuid = Uuid.random()
) = ProjectState(
    id = id,
    title = title,
    projectId = projectId
)