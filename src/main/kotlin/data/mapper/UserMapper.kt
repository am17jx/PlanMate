package org.example.data.mapper

import org.example.data.models.UserDTO
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