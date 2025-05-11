package mockdata

import org.example.logic.models.Project
import org.example.logic.models.ProjectState

fun createProject(
    id: String = "",
    name: String = "",
    projectStates: List<ProjectState> = emptyList(),
    auditLogsIds: List<String> = emptyList()
) = Project(
    id = id,
    name = name,
    projectStateIds = projectStates,
    auditLogsIds = auditLogsIds
)

fun createState(
    id: String = "",
    title: String = ""
) = ProjectState(
    id = id,
    title = title
)