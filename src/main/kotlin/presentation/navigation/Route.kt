package org.example.presentation.navigation

import org.example.logic.models.UserRole

sealed interface Route {
    data object LoginRoute : Route
    data object AdminHomeRoute : Route
    data class ProjectsOverviewUI(val userRole: UserRole) : Route
    data object CreateProjectRoute : Route
    data class ShowProjectTasksRoute(val projectId: String) : Route
    data class ProjectStatusRoute(val projectId: String) : Route
    data object CreateUserRoute : Route
    data class TaskDetailsRoute(val taskId: String) : Route

}

