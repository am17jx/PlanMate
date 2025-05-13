package mockdata

import org.example.logic.models.Task

fun createTask(
    id: String = "",
    name: String = "",
    addedBy: String = "",
    projectId: String = "",
    stateId: String = "",
    auditLogsIds: List<String> = emptyList()
) = Task(
    id = id,
    name = name,
    stateId = stateId,
    addedBy = addedBy,
    auditLogsIds = auditLogsIds,
    projectId = projectId
)