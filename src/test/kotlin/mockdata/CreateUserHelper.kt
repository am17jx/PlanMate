package mockdata

import org.example.logic.models.User
import org.example.logic.models.UserRole
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun createUser(
    id: Uuid = Uuid.random(),
    username: String = "",
    role: UserRole = UserRole.ADMIN,
    authMethod: User.AuthenticationMethod = User.AuthenticationMethod.Password("")
) = User(
    id = id,
    username = username,
    role = role,
    authMethod = authMethod
)