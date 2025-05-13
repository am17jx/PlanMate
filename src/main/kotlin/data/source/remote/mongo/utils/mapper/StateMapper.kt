package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.StateDTO
import org.example.logic.models.State


fun StateDTO.toState(): State {
    return State(id = id, title = title)
}

fun State.toStateDTO(): StateDTO {
    return StateDTO(id = id, title = title)
}
