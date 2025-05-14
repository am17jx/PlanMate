package org.example.data.source.local.csv.utils.mapper

import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.utils.toUuid
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
fun User.toCsvRow(): String {
    val authMethodType = when (authMethod) {
        is User.AuthenticationMethod.Password -> "PASSWORD"
    }

    val authMethodValue = when (authMethod) {
        is User.AuthenticationMethod.Password -> authMethod.password
    }

    return "${id.toHexString()},$username,$role,$authMethodType,$authMethodValue"
}

fun List<User>.toCsvRows(): List<String> = this.map {
    it.toCsvRow()
}

@OptIn(ExperimentalUuidApi::class)
fun List<String>.toUsers(): List<User> {
    val usersList = mutableListOf<User>()

    this.forEach { row ->
        val partsOfUser = row.split(",")
        val id = partsOfUser[0].toUuid()
        val username = partsOfUser[1]
        val role = UserRole.valueOf(partsOfUser[2])
        val authMethodType = partsOfUser[3]
        val authMethodValue = partsOfUser[4]

        val authMethod = when (authMethodType) {
            "PASSWORD" -> User.AuthenticationMethod.Password(authMethodValue)
            else -> throw IllegalArgumentException("Unknown auth method type: $authMethodType")
        }

        usersList.add(User(id, username, role, authMethod))
    }

    return usersList
}
