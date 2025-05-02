package org.example.presentation

import org.example.logic.models.UserRole
import org.example.presentation.navigation.NavigationCallBack
import org.example.presentation.navigation.NavigationController
import org.example.presentation.navigation.Route
import org.example.presentation.role.AdminOptions
import org.example.presentation.role.MateOptions
import org.example.presentation.role.SharedOptions
import org.example.presentation.screens.*
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
                        2 -> navigationController.navigateTo(Route.CreateProjectRoute)
                        3 -> println("Create User - Coming soon!")
                        4 -> navigationController.popBackStack()
                    }
                }
            }

            is Route.ShowAllProjectsRoute -> {
//                ShowAllProjectsUI(userFactory(route.userRole)) {
//                    navigationController.popBackStack()
//                }
            }

            is Route.CreateProjectRoute -> {
                CreateNewProjectUi(
                    createProjectUseCase = getKoin().get(),
                    onBack = { navigationController.navigateTo(Route.AdminHomeRoute) }
                )
            }

            is Route.ShowProjectTasksRoute -> {
                ShowProjectTasksUI.create(
                    projectId = route.projectId,
                    onNavigateBack = navigationController::popBackStack,
                    onNavigateToTaskDetails = {
                        //TODO("navigate to task details")
                        println("navigating to task details with id $it")
                    }
                )
            }
        }
    }

    override fun onFinish() {
        println("Exiting...")
        exitProcess(0)
    }

    private fun userFactory(type: UserRole): SharedOptions {
        return when (type) {
            UserRole.ADMIN -> AdminOptions()
            UserRole.USER -> MateOptions()
        }
    }
}
