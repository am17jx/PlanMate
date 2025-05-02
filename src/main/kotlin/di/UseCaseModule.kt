package di

import org.example.logic.useCase.GetAllProjectsUseCase
import org.example.logic.useCase.LoginUserUseCase
import org.example.logic.useCase.creatTask.CreateTaskUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val useCaseModule =
    module {
        singleOf(::LoginUserUseCase)
        singleOf(::CreateTaskUseCase)
        singleOf(::GetAllProjectsUseCase)
    }
