package di

import org.example.data.repository.TaskRepositoryImpl
import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.TaskRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val repositoryModule = module {
    single <AuthenticationRepository>{object :AuthenticationRepository{
        override fun getCurrentUser(): User? {
            TODO("Not yet implemented")
        }

        override fun createMate(username: String, hashedPassword: String): User {
            TODO("Not yet implemented")
        }

        override fun login(username: String, hashedPassword: String): User {
            TODO("Not yet implemented")
        }

        override fun getAllUsers(): List<User> {
            TODO("Not yet implemented")
        }
    }}
    singleOf(::TaskRepositoryImpl) { bind<TaskRepository>() }
}