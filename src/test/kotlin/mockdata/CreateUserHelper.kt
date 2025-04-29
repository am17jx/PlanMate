package mockdata

import org.example.logic.models.User
import org.example.logic.models.UserRole

fun createUser(
    id: String = "",
    username: String = "",
    password: String = "",
    role: UserRole = UserRole.ADMIN
) = User(
    id = id,
    username = username,
    password = password,
    role = role
)