package di

import org.example.data.source.remote.RoleValidationInterceptor
import org.example.di.dataSourceModule
import org.koin.dsl.module

val appModule =
    module {
        includes(uiModule, repositoryModule, useCaseModule, dataSourceModule)
        single { ::RoleValidationInterceptor }
    }
