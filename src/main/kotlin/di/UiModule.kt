package di

import org.example.logic.models.UserRole
import org.example.logic.useCase.CreateMateUseCase
import org.example.presentation.MainUiController
import org.example.presentation.navigation.NavigationController
import org.example.presentation.navigation.Route
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import presentation.utils.io.ConsoleReader
import presentation.utils.io.ConsoleViewer
import presentation.utils.io.Reader
import presentation.utils.io.Viewer
import org.koin.dsl.module
import presentation.utils.TablePrinter

val uiModule =
    module {
        single { NavigationController(startDestination = Route.ProjectsOverviewUI(userRole = UserRole.ADMIN)) }
        singleOf(::MainUiController)
        singleOf(::ConsoleReader) { bind<Reader>() }
        singleOf(::ConsoleViewer) { bind<Viewer>() }
        singleOf(::TablePrinter)
        single<Viewer> { ConsoleViewer() }
        single<Reader> { ConsoleReader() }

        single { NavigationController(startDestination = Route.LoginRoute) }

        single { MainUiController(get(), get(), get()) }

        single { CreateMateUseCase(authenticationRepository = get()) }
    }
