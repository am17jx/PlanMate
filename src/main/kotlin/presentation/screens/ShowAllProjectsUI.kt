package org.example.presentation.screens

import org.example.presentation.role.ProjectScreens

class ShowAllProjectsUI(
    private val projectScreens: ProjectScreens,
    private val onBack:() -> Unit
) {
    init {
        projectScreens.showAllProjects()
        if (readln()=="0"){
            onBack()
        }
    }
}