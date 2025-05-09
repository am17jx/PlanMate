package di

import org.example.data.source.remote.RoleValidationInterceptor
import org.example.di.dataSourceModule
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule =
    module {
        includes(uiModule, repositoryModule, useCaseModule, dataSourceModule)
        singleOf(::RoleValidationInterceptor)
    }
