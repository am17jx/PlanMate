package org.example.data.source.remote.mongo.utils.mapper

import org.example.data.source.remote.models.UserDTO
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.utils.toUuid
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun UserDTO.toUser(): User =
    User(
        id = id.toUuid(),
        username = username,
        role = UserRole.valueOf(role),
        authMethod = authMethod.toAuthMethod()
    )

@OptIn(ExperimentalUuidApi::class)
fun User.toUserDTO(): UserDTO =
    UserDTO(
        id = id.toHexString(),
        username = username,
        role = role.name,
        authMethod = authMethod.toAuthMethodDto()
    )
fun User.AuthenticationMethod.toAuthMethodDto(): UserDTO.AuthenticationMethodDto = when(this){
    is User.AuthenticationMethod.Password -> UserDTO.AuthenticationMethodDto.Password(this.password)
}

fun UserDTO.AuthenticationMethodDto.toAuthMethod(): User.AuthenticationMethod = when(this){
    is UserDTO.AuthenticationMethodDto.Password -> User.AuthenticationMethod.Password(this.password)
}