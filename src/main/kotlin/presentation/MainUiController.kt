package org.example.presentation

import org.example.logic.models.UserRole
import org.example.presentation.navigation.NavigationCallBack
import org.example.presentation.navigation.NavigationController
import org.example.presentation.navigation.Route
import org.example.presentation.role.Admin
import org.example.presentation.role.Mate
import org.example.presentation.role.User
import org.example.presentation.screens.AdminHomeUI
import org.example.presentation.screens.LoginUI
import org.example.presentation.screens.ShowAllProjectsUI
import org.koin.java.KoinJavaComponent.getKoin
import kotlin.system.exitProcess


class MainUiController(
    private val navigationController: NavigationController
) : NavigationCallBack {

    init {
        navigationController.registerNavigationCallBack(this)
    }

    override fun onNavigate(route: Route) {
        when (route) {
            is Route.LoginRoute -> {
                LoginUI(
                    onLoginSuccess = {
                        when (it) {
                            UserRole.ADMIN -> navigationController.navigateTo(Route.AdminHomeRoute)
                            UserRole.USER -> navigationController.navigateTo(Route.ShowAllProjectsRoute(userRole = it))
                        }
                    },
                    loginUserUseCase = getKoin().get()
                )
            }

            is Route.AdminHomeRoute -> {
                AdminHomeUI { choice ->
                    when (choice) {
                        1 -> navigationController.navigateTo(Route.ShowAllProjectsRoute(UserRole.ADMIN))
                        2 -> println("Create Project - Coming soon!")
                        3 -> println("Create User - Coming soon!")
                        4 -> navigationController.popBackStack()
                    }
                }
            }

            is Route.ShowAllProjectsRoute -> {
                ShowAllProjectsUI(userFactory(route.userRole)) {
                    navigationController.popBackStack()
                }
            }
        }
    }

    override fun onFinish() {
        println("Exiting...")
        exitProcess(0)
    }

    private fun userFactory(type: UserRole): User {
        return when (type) {
            UserRole.ADMIN -> Admin()
            UserRole.USER -> Mate()
        }
    }
}
