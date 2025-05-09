package di

import logic.useCase.CreateTaskUseCase
import org.example.logic.useCase.*
import org.example.logic.useCase.DeleteProjectUseCase
import org.example.logic.useCase.DeleteTaskUseCase
import org.example.logic.useCase.UpdateTaskUseCase
import org.example.logic.useCase.updateProject.UpdateProjectUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val useCaseModule =
    module {
        singleOf(::DeleteProjectUseCase)
        singleOf(::DeleteTaskUseCase)
        singleOf(::CreateMateUseCase)
        singleOf(::CreateProjectUseCase)
        singleOf(::CreateStateUseCase)
        singleOf(::CreateTaskUseCase)
        singleOf(::DeleteStateUseCase)
        singleOf(::GetAllProjectsUseCase)
        singleOf(::GetCurrentUserUseCase)
        singleOf(::GetEntityAuditLogsUseCase)
        singleOf(::GetProjectByIdUseCase)
        singleOf(::GetProjectTasksUseCase)
        singleOf(::GetStateNameUseCase)
        singleOf(::GetTaskByIdUseCase)
        singleOf(::LoginUserUseCase)
        singleOf(::UpdateProjectUseCase)
        singleOf(::UpdateStateUseCase)
        singleOf(::UpdateTaskUseCase)
    }
