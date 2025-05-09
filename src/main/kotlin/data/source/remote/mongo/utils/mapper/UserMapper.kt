package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.UserDTO
import org.example.logic.models.User
import org.example.logic.models.UserRole

fun UserDTO.toUser(): User {
    return User(
        id = _id,
        username = username,
        password = password,
        role = UserRole.valueOf(role)
    )
}

fun User.toUserDTO(): UserDTO {
    return UserDTO(
        _id = id,
        username = username,
        password = password,
        role = role.name
    )
}