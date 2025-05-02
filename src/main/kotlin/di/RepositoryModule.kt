package di

import org.example.data.repository.AuditLogRepositoryImpl
import org.example.data.repository.AuthenticationRepositoryImpl
import org.example.data.repository.ProjectRepositoryImpl
import org.example.data.repository.TaskRepositoryImpl
import org.example.logic.repositries.AuditLogRepository
import org.example.logic.repositries.AuthenticationRepository
import org.example.logic.repositries.ProjectRepository
import org.example.logic.repositries.TaskRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::TaskRepositoryImpl) { bind<TaskRepository>() }
        singleOf(::ProjectRepositoryImpl) { bind<ProjectRepository>() }
        singleOf(::AuditLogRepositoryImpl) { bind<AuditLogRepository>() }
        singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
    }
