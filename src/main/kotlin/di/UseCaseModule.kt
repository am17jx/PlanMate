package di

import logic.useCase.*
import org.example.logic.useCase.LoginUserUseCase
import org.example.logic.useCase.CreateMateUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val useCaseModule =
    module {
        singleOf(::LoginUserUseCase)
        singleOf(::CreateMateUseCase)
    }
