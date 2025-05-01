package di

import org.koin.dsl.module
import org.example.presentation.MainUiController
import org.example.presentation.navigation.NavigationController
import org.example.presentation.navigation.Route
import presentation.utils.io.ConsoleReader
import presentation.utils.io.ConsoleViewer
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

val uiModule = module {
    single<Viewer> { ConsoleViewer() }
    single<Reader> { ConsoleReader() }

    single { NavigationController(startDestination = Route.LoginRoute) }

    single { MainUiController(get(), get(), get()) }
}
