package di

import org.example.data.repository.AuthenticationRepositoryImpl
import org.example.logic.models.User
import org.example.logic.repositries.AuthenticationRepository
import org.koin.dsl.module


val repositoryModule = module {
    single <AuthenticationRepository> {AuthenticationRepositoryImpl(get())}
}