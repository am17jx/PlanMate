package org.example.data.source.local.csv

import org.example.data.source.local.contract.LocalAuthenticationDataSource
import org.example.data.source.local.csv.utils.CSVReader
import org.example.data.source.local.csv.utils.CSVWriter
import org.example.data.source.local.csv.utils.mapper.toCsvRow
import org.example.data.source.local.csv.utils.mapper.toUsers
import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.utils.UserAlreadyExistsException
import org.example.logic.utils.UserNotFoundException

class CsvAuthenticationDataSource(
    private val csvWriter: CSVWriter,
    private val csvReader: CSVReader
) : LocalAuthenticationDataSource {

    private var currentUser: User? = null

    override fun saveUser(user: User) {
        if (isUserExists(user.username)) {
            throw UserAlreadyExistsException("User already exists")
        }
        val allUsersRows = csvReader.readLines() + user.toCsvRow()
        csvWriter.writeLines(allUsersRows)
    }

    override fun getAllUsers(): List<User> {
        return csvReader.readLines().toUsers()
    }

    override fun login(username: String, hashedPassword: String): User {
        if (isUserNotFound(username, hashedPassword)) {
            throw UserNotFoundException("User not found")
        }
        val userId = getUserId(username, hashedPassword)
        val userRole = getUserRole(username, hashedPassword)
        currentUser = User(userId, username, hashedPassword, userRole)
        return currentUser!!
    }

    override fun getCurrentUser(): User? = currentUser


    private fun getUserId(username: String, hashedPassword: String): String {
        try {
            return getAllUsers().first { it.username == username && it.password == hashedPassword }.id
        } catch (e: Exception) {
            throw UserNotFoundException("User not found")
        }

    }

    private fun getUserRole(username: String, hashedPassword: String): UserRole {
        try {
            return getAllUsers().first { it.username == username && it.password == hashedPassword }.role
        } catch (e: Exception) {
            throw UserNotFoundException("User not found")
        }
    }

    private fun isUserNotFound(username: String, password: String) =
        getAllUsers().none { it.username == username && it.password == password }

    private fun isUserExists(username: String) = getAllUsers().any { it.username == username }
}