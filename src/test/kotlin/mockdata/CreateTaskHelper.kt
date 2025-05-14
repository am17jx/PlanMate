package mockdata

import org.example.logic.models.Task
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun createTask(
    id: Uuid = Uuid.random(),
    name: String = "",
    addedById: Uuid = Uuid.random(),
    addedByName: String = "",
    projectId: Uuid = Uuid.random(),
    stateId: Uuid = Uuid.random(),
    stateName: String = "",
) = Task(
    id = id,
    name = name,
    stateId = stateId,
    addedById = addedById,
    addedByName = addedByName,
    projectId = projectId,
    stateName = stateName
)