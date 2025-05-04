package org.example.data.source.local.csv.utils.mapper

import org.example.logic.models.User
import org.example.logic.models.UserRole

fun User.toCsvRow(): String {
    return "$id,$username,$password,$role"
}

fun List<User>.toCsvRows(): List<String> {
    val listOfRows = mutableListOf<String>()

    return this.map {
        it.toCsvRow()
    }

}

fun List<String>.toUsers(): List<User> {
    val usersList = mutableListOf<User>()

    this.forEach { row ->
        val partsOfUser = row.split(",")
        usersList.add(User(partsOfUser[0],partsOfUser[1],partsOfUser[2], UserRole.valueOf(partsOfUser[3])))
    }

    return usersList
}