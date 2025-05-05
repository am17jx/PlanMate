package org.example.data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.toList
import org.example.data.mapper.toUser
import org.example.data.mapper.toUserDTO
import org.example.data.models.UserDTO
import org.example.data.source.remote.contract.RemoteAuthenticationDataSource
import org.example.logic.models.User

class MongoAuthenticationDataSource(private val mongoClient: MongoCollection<UserDTO>): RemoteAuthenticationDataSource {
    override suspend fun saveUser(user: User) {
        mongoClient.insertOne(user.toUserDTO())
    }

    override suspend fun getAllUsers(): List<User> {
        return mongoClient.find().toList().map { it.toUser() }
    }

}