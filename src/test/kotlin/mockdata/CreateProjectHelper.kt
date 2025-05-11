package mockdata

import org.example.logic.models.Project
import org.example.logic.models.State

fun createProject(
    id: String = "",
    name: String = "",
    states: List<State> = emptyList(),
    auditLogsIds: List<String> = emptyList()
) = Project(
    id = id,
    name = name,
    projectStateIds = states,
    auditLogsIds = auditLogsIds
)

fun createState(
    id: String = "",
    title: String = ""
) = State(
    id = id,
    title = title
)