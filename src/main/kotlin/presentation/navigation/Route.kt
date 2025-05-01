package org.example.presentation.navigation

import org.example.logic.models.UserRole

sealed interface Route {
    data object LoginRoute : Route
    data object AdminHomeRoute : Route
    data class ShowAllProjectsRoute(val userRole: UserRole) : Route
}