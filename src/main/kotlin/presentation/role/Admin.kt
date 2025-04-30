package org.example.presentation.role

class Admin: User() {
    override fun showAllProjects() {
        super.showAllProjects()
        println("update project")
        println("delete project")
    }
}