package di

import logic.useCase.*
import org.example.logic.useCase.GetProjectByIdUseCase
import org.example.logic.useCase.GetProjectTasksUseCase
import org.example.logic.useCase.GetAllProjectsUseCase
import org.example.logic.useCase.LoginUserUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val useCaseModule =
    module {
        singleOf(::LoginUserUseCase)
        singleOf(::CreateTaskUseCase)
        singleOf(::GetAllProjectsUseCase)
        singleOf(::GetProjectTasksUseCase)
        singleOf(::GetProjectByIdUseCase)
    }
