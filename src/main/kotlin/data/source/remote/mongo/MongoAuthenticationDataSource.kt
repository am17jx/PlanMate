package org.example.data.source.remote.mongo

import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.toList
import org.example.data.mapper.toUser
import org.example.data.mapper.toUserDTO
import org.example.data.source.remote.models.UserDTO
import org.example.data.source.remote.contract.RemoteAuthenticationDataSource
import org.example.logic.models.User
import org.example.logic.utils.CreationItemFailedException
import org.example.logic.utils.GetItemsFailedException

class MongoAuthenticationDataSource(private val mongoClient: MongoCollection<UserDTO>): RemoteAuthenticationDataSource {
    override suspend fun saveUser(user: User) {

        try {
            mongoClient.insertOne(user.toUserDTO())
        } catch (e: Exception) {
            throw CreationItemFailedException("Failed to create user ${e.message}")
        }
    }

    override suspend fun getAllUsers(): List<User> {
        try {
            return mongoClient.find().toList().map { it.toUser() }
        } catch (e: Exception) {
            throw GetItemsFailedException("Failed to get users ${e.message}")
        }
    }

}