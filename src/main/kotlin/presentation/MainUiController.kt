package org.example.presentation

import org.example.logic.models.UserRole
import org.example.presentation.navigation.NavigationCallBack
import org.example.presentation.navigation.NavigationController
import org.example.presentation.navigation.Route
import org.example.presentation.role.Admin
import org.example.presentation.role.Mate
import org.example.presentation.role.User
import org.example.presentation.screens.*
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.system.exitProcess
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class MainUiController(
    private val navigationController: NavigationController,
    private val viewer: Viewer,
    private val reader: Reader
) : NavigationCallBack {

    init {
        navigationController.registerNavigationCallBack(this)
    }

    override fun onNavigate(route: Route) {
        when (route) {
            is Route.LoginRoute -> {
                LoginUI(
                    onNavigateToAdminHome = {navigationController.navigateTo(Route.AdminHomeRoute)},
                    onNavigateToShowAllProjects = { navigationController.navigateTo(Route.ShowAllProjectsRoute(it))},
                    loginUserUseCase = getKoin().get(),
                    reader = reader,
                    viewer = viewer
                )
            }

            is Route.AdminHomeRoute -> {
                AdminHomeUI(
                    onNavigateToShowAllProjectsUI = { navigationController.navigateTo(Route.ShowAllProjectsRoute(it)) },
                    onNavigateToCreateProject = { navigationController.navigateTo(Route.CreateProjectRoute) },
                    onNavigateToCreateUser = { navigationController.navigateTo(Route.CreateUserRoute) },
                    onNavigateToOnBackStack = { navigationController.popBackStack() },
                    viewer = viewer,
                    reader = reader,
                    userRole = UserRole.ADMIN
                )
            }

            is Route.ShowAllProjectsRoute -> {
                ShowAllProjectsUI(userFactory(route.userRole)) {
                    navigationController.popBackStack()
                }
            }

            is Route.CreateProjectRoute -> {
                CreateNewProjectUi(
                    createProjectUseCase = getKoin().get(),
                    onBack = { navigationController.navigateTo(Route.AdminHomeRoute) },
                    reader = reader,
                    viewer = viewer
                )
            }

            is Route.CreateUserRoute -> {
                CreateUserUi(
                    createMateUseCase = getKoin().get(),
                    reader = reader,
                    viewer = viewer,
                    onBack = { navigationController.popBackStack() }
                )
            }

        }
    }

    override fun onFinish() {
        viewer.display("Exiting...")
        exitProcess(0)
    }

    private fun userFactory(type: UserRole): User {
        return when (type) {
            UserRole.ADMIN -> Admin()
            UserRole.USER -> Mate()
        }
    }
}
