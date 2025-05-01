package org.example.presentation.role

class AdminOptions : SharedOptions() {
    override fun showAllProjectsOptions():List<Map<String,String>> {
        return  super.showAllProjectsOptions() + listOf(
            mapOf("2" to "2- update project"),
            mapOf("3" to "3- delete project")
        )
    }
}