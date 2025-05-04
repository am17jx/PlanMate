package org.example.data.source.local.contract

import org.example.logic.models.User

interface LocalAuthenticationDataSource {
    fun saveUser(user: User)
    fun getAllUsers(): List<User>
}