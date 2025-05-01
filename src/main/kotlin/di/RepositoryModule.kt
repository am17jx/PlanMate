package di

import org.example.logic.models.User
import org.example.logic.models.UserRole
import org.example.logic.repositries.AuthenticationRepository
import org.koin.dsl.module
import java.util.*

val repositoryModule = module {
    single<AuthenticationRepository> {
        object : AuthenticationRepository {

            private val users = mutableListOf(
                User(
                    id = UUID.randomUUID().toString(),
                    username = "sara",
                    password = "fcea920f7412b5da7be0cf42b8c93759", // "1234567" hashed with MD5
                    role = UserRole.USER
                ),
                User(
                    id = UUID.randomUUID().toString(),
                    username = "admin",
                    password = "21232f297a57a5a743894a0e4a801fc3", // "admin" hashed with MD5
                    role = UserRole.ADMIN
                )
            )

            private var currentUser: User? = null

            override fun getCurrentUser(): User? = currentUser

            override fun createMate(username: String, hashedPassword: String): User {
                val newUser = User(
                    id = UUID.randomUUID().toString(),
                    username = username,
                    password = hashedPassword,
                    role = UserRole.USER
                )
                users.add(newUser)
                return newUser
            }

            override fun login(username: String, hashedPassword: String): User {
                val user = users.first { it.username == username && it.password == hashedPassword }
                currentUser = user
                return user
            }

            override fun getAllUsers(): List<User> = users
        }
    }
}
