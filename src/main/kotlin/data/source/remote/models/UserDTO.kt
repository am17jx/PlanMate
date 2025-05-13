@file:OptIn(ExperimentalSerializationApi::class)

package org.example.data.source.remote.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class UserDTO(
    @BsonId val id: String,
    val username: String,
    val role: String,
    val authMethod: AuthenticationMethodDto
) {
    @Serializable
    @JsonClassDiscriminator("type")
    sealed class AuthenticationMethodDto{
        @Serializable
        @SerialName("password")
        data class Password(val password: String): AuthenticationMethodDto()
    }
}
