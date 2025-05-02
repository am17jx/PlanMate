package di

import org.example.logic.models.UserRole
import org.example.presentation.MainUiController
import org.example.presentation.navigation.NavigationController
import org.example.presentation.navigation.Route
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.utils.TablePrinter
import presentation.utils.io.ConsoleReader
import presentation.utils.io.ConsoleViewer
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

val uiModule =
    module {
        single { NavigationController(startDestination = Route.LoginRoute) }
        singleOf(::MainUiController)
        singleOf(::ConsoleReader) { bind<Reader>() }
        singleOf(::ConsoleViewer) { bind<Viewer>() }
        singleOf(::TablePrinter)
    }
