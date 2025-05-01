package org.example.presentation.screens

import org.example.logic.models.UserRole
import presentation.utils.io.Reader
import presentation.utils.io.Viewer

class AdminHomeUI(
    private val viewer: Viewer,
    private val reader: Reader,
    private val userRole: UserRole,
    private val onNavigateToShowAllProjectsUI: (userRole: UserRole) -> Unit,
    private val onNavigateToCreateProject: () -> Unit,
    private val onNavigateToCreateUser: () -> Unit,
    private val onNavigateToOnBackStack: () -> Unit
) {
    init {
        showMenu()
    }

    private fun showMenu() {
        viewer.display("\n===== Admin Home =====")
        viewer.display("1. Show All Projects")
        viewer.display("2. Create New Project")
        viewer.display("3. Create User")
        viewer.display("4. Back")
        viewer.display("Enter your choice: ")

        val choice = reader.readInt()
        when (choice) {
            1 -> onNavigateToShowAllProjectsUI(userRole)
            2 -> onNavigateToCreateProject()
            3 -> onNavigateToCreateUser()
            4 -> onNavigateToOnBackStack()
            else -> {
                viewer.display("Invalid input. Try again.")
                showMenu()
            }
        }
    }
}
