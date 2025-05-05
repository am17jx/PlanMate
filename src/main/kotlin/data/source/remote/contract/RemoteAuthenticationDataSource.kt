package org.example.data.source.remote.contract

import org.example.logic.models.User

interface RemoteAuthenticationDataSource {

    suspend fun saveUser(user: User)
    suspend fun getAllUsers(): List<User>
}