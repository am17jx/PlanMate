package org.example.presentation.role

class AdminOptions : SharedOptions() {
    override fun showAllProjectsOptions(): Map<String, String> {
        return super.showAllProjectsOptions() + mapOf(
            "2" to "2- update project",
            "3" to "3- delete project",
            "4" to "4- show projects logs"
        )
    }
}