package org.example.data.mapper

import org.example.data.models.UserDTO
import org.example.logic.models.User

fun UserDTO.toUser(): User {
    return User(
        id = id,
        username = username,
        password = password,
        role = role
    )
}

fun User.toUserDTO(): UserDTO {
    return UserDTO(
        id = id,
        username = username,
        password = password,
        role = role
    )
}