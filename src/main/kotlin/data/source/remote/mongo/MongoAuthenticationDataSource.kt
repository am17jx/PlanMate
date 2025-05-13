package org.example.data.source.remote.mongo

import com.mongodb.client.model.Filters
import com.mongodb.kotlin.client.coroutine.MongoCollection
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import org.example.data.source.remote.models.UserDTO
import org.example.data.source.remote.mongo.utils.executeMongoOperation
import org.example.data.source.remote.mongo.utils.mapper.toUser
import org.example.data.source.remote.mongo.utils.mapper.toUserDTO
import org.example.data.repository.sources.remote.RemoteAuthenticationDataSource
import org.example.data.utils.Constants.AUTH_TYPE_FIELD
import org.example.data.utils.Constants.PASSWORD_FIELD
import org.example.data.utils.Constants.USERNAME_FIELD
import org.example.logic.models.User
import org.example.logic.utils.UserAlreadyExistsException
import org.example.logic.utils.UserNotFoundException

class MongoAuthenticationDataSource(private val mongoClient: MongoCollection<UserDTO>) :
    RemoteAuthenticationDataSource {

    private var currentUser: User? = null

    override suspend fun saveUser(user: User) {

        if (mongoClient.find(Filters.eq(USERNAME_FIELD, user.username)).firstOrNull() != null)
            throw UserAlreadyExistsException()
        executeMongoOperation {
            mongoClient.insertOne(user.toUserDTO())
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return executeMongoOperation {
            mongoClient.find().toList().map { it.toUser() }
        }
    }

    override suspend fun loginWithPassword(username: String, hashedPassword: String): User {
        return executeMongoOperation {
            currentUser = mongoClient
                .find(
                    Filters.and(
                        Filters.eq(USERNAME_FIELD, username),
                        Filters.eq(AUTH_TYPE_FIELD, "password"),
                        Filters.eq(PASSWORD_FIELD, hashedPassword)
                    )
                )
                .firstOrNull()?.toUser()
                ?: throw UserNotFoundException()
            currentUser!!
        }
    }

    override suspend fun logout() {
        currentUser = null
    }

    override suspend fun getCurrentUser(): User? = currentUser
}
