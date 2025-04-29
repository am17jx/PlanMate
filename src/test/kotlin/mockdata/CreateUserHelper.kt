package mockdata

import org.example.logic.models.User
import org.example.logic.models.UserRole

fun createUser(
    role: UserRole
) = User(
    id = "1",
    username = "",
    password = "",
    role = role
)