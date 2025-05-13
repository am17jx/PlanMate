package org.example.logic.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class User(
    val id: Uuid = Uuid.random(),
    val username: String,
    val role: UserRole,
    val authMethod: AuthenticationMethod,
) {
    sealed class AuthenticationMethod {
        data class Password(val password: String) : AuthenticationMethod()
    }
}
