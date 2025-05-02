package di

import logic.useCase.*
import org.example.logic.useCase.deleteProject.DeleteProjectUseCase
import org.example.logic.useCase.deleteTask.DeleteTaskUseCase
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
