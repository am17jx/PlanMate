package di

import org.example.data.repository.*
import org.example.logic.repositries.*
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule =
    module {
        singleOf(::TaskRepositoryImpl) { bind<TaskRepository>() }
        singleOf(::ProjectRepositoryImpl) { bind<ProjectRepository>() }
        singleOf(::AuditLogRepositoryImpl) { bind<AuditLogRepository>() }
        singleOf(::TaskRepositoryImpl) { bind<TaskRepository>() }
        singleOf(::AuthenticationRepositoryImpl) { bind<AuthenticationRepository>() }
        singleOf(::TaskStateRepositoryImpl) { bind<TaskStateRepository>() }

    }
